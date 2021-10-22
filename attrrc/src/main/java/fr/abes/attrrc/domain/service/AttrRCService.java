package fr.abes.attrrc.domain.service;

import fr.abes.attrrc.domain.dto.RCDto;
import fr.abes.attrrc.domain.entity.*;
import fr.abes.attrrc.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Slf4j
@Service
public class AttrRCService {

    private final OracleProxy oracleProxy;

    // This method returns filter function which will log request data
    // Using this for DEBUG mod
    private static ExchangeFilterFunction logRequestWebclient() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    public Mono<RCDto> attributs(String ppn) {


        RCDto rcDto = new RCDto();
        String ppnVal = ppn.substring(0, ppn.indexOf("-"));
        int posVal = Integer.parseInt(ppn.substring(ppn.indexOf("-") + 1));

        Mono<XmlRootRecord> xmlRootRecord = oracleProxy.getXmlRootRecord(ppnVal);

        return xmlRootRecord.publishOn(Schedulers.boundedElastic())
                .flatMap(v -> {

                    Predicate<Datafield> datafieldPredicateTag035 = t -> t.getTag().equals("035");
                    Predicate<Datafield> datafieldPredicateTag101 = t -> t.getTag().equals("101");
                    Predicate<Datafield> datafieldPredicateTag210 = t -> t.getTag().equals("210");
                    Predicate<Datafield> datafieldPredicateTag200 = t -> t.getTag().equals("200");
                    Predicate<Datafield> datafieldPredicateTag600 = t -> t.getTag().equals("600");
                    Predicate<Datafield> datafieldPredicateTag606 = t -> t.getTag().equals("606");
                    Predicate<Datafield> datafieldPredicateTag607 = t -> t.getTag().equals("607");
                    Predicate<Datafield> datafieldPredicateTag608 = t -> t.getTag().equals("608");
                    Predicate<Datafield> datafieldPredicateTag676 = t -> t.getTag().equals("676");
                    Predicate<Subfield> subfieldPredicateCode3 = t -> t.getCode().equals("3");
                    Predicate<Subfield> subfieldPredicateCode4 = t -> t.getCode().equals("4");
                    Predicate<Subfield> subfieldPredicateCodeA = t -> t.getCode().equals("a");
                    Predicate<Subfield> subfieldPredicateCodeB = t -> t.getCode().equals("b");
                    Predicate<Subfield> subfieldPredicateCodeC = t -> t.getCode().equals("c");
                    Predicate<Subfield> subfieldPredicateCodeD = t -> t.getCode().equals("d");
                    Predicate<Subfield> subfieldPredicateCodeF = t -> t.getCode().equals("f");
                    Predicate<Subfield> subfieldPredicateCodeY = t -> t.getCode().equals("y");
                    Predicate<Subfield> subfieldPredicateCodeX = t -> t.getCode().equals("x");
                    Predicate<Subfield> subfieldPredicateCodeZ = t -> t.getCode().equals("z");

                    //Set ID
                    rcDto.setId(ppn);
                    // Set dateCreationNotice
                    v.getControlfieldList()
                            .stream()
                            .filter(t -> t.getTag().equals("004"))
                            .findFirst()
                            .ifPresent(t -> rcDto.setDateCreationNotice(t.getControlfield()));

                    // Set Citation

                    // Set PpnAuth & Appellation

                    List<Datafield> datafields = v.getDatafieldList().stream()
                            .filter(t -> t.getTag().startsWith("70")).collect(Collectors.toList());

                    Supplier<Stream<Subfield>> streamSupplier = () -> IntStream.range(0, datafields.size()).filter(i -> i == posVal - 1)
                            .mapToObj(datafields::get)
                            .flatMap(t -> t.getSubfieldList().stream());


                    streamSupplier.get().filter(subfieldPredicateCode3).findFirst().ifPresent(t -> {
                        rcDto.setPpnAut(t.getSubfield());
                    });

                    StringBuilder appellation = streamSupplier.get().filter(subfieldPredicateCodeA.or(subfieldPredicateCodeB))
                            .map(t -> new StringBuilder(t.getSubfield()))
                            .reduce(new StringBuilder(), (a, b) -> {
                                if (a.length() > 0) {
                                    a.append(" ");
                                }
                                a.append(b);
                                return a;
                            });

                    rcDto.setAppellation(appellation.toString());

                    // Set Role_Code
                    String roleCode = streamSupplier.get().filter(subfieldPredicateCode4)
                            .limit(1)
                            .map(Subfield::getSubfield)
                            .map(Object::toString)
                            .collect(Collectors.joining());
                    rcDto.setRole_code(roleCode);


                    // Set Rameau

                    List<Datafield> datafieldsRameau = v.getDatafieldList().stream()
                            .filter(datafieldPredicateTag606).collect(Collectors.toList());

                    rcDto.setRameau(datafieldsRameau.stream().map(t -> {
                                        List<String> rameau = new ArrayList<>();
                                        StringBuilder stringBuilderRameau = getReduceStringBuilderWith3Predicate(subfieldPredicateCodeA,
                                                subfieldPredicateCodeY,
                                                subfieldPredicateCodeZ,
                                                " -- ",
                                                t);
                                        rameau.add(stringBuilderRameau.toString());
                                        return rameau;
                                    })
                                    .flatMap(List::stream)
                                    .collect(Collectors.toList())
                    );

                    // Set Subject 600 et 600 new

                    StringBuilder subject600 = v.getDatafieldList().stream().filter(datafieldPredicateTag600)
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeA.or(subfieldPredicateCodeC).or(subfieldPredicateCodeF))
                            .map(t -> new StringBuilder(t.getSubfield()))
                            .reduce(new StringBuilder(), (a, b) -> {
                                if (a.length() > 0) {
                                    a.append(",");
                                }
                                a.append(b);
                                return a;
                            });

                    StringBuilder subject600New = new StringBuilder();
                    List<String> listOfSubject600andNew = new ArrayList<>(Arrays.asList(subject600.toString().split(",")));
                    subject600New.append(listOfSubject600andNew.subList(0, 1)
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining()));
                    subject600New.append(listOfSubject600andNew.subList(1, listOfSubject600andNew.size())
                            .stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining("", "(", ")")));

                    rcDto.setSubject600(subject600.toString());
                    rcDto.setSubject600new(subject600New.toString());


                    // Set 607

                    List<Datafield> datafields607 = v.getDatafieldList().stream()
                            .filter(datafieldPredicateTag607).collect(Collectors.toList());

                    rcDto.setSubject607(datafields607.stream().map(t -> {
                                        List<String> subfield607 = new ArrayList<>();
                                        StringBuilder stringBuilder607 = getReduceStringBuilderWith3Predicate(
                                                subfieldPredicateCodeA,
                                                subfieldPredicateCodeX, subfieldPredicateCodeZ,
                                                ", ",t);
                                        subfield607.add(stringBuilder607.toString());
                                        return subfield607;
                                    })
                                    .flatMap(List::stream)
                                    .collect(Collectors.toList())
                    );

                    // Set 608

                    findSubfield(v, datafieldPredicateTag608, subfieldPredicateCodeA)
                            .ifPresent(t -> rcDto.setSubject608(t.getSubfield()));

                    // Set Set dewey

                    findSubfield(v, datafieldPredicateTag676, subfieldPredicateCodeA)
                            .ifPresent(t -> rcDto.setDewey(t.getSubfield()));

                    // Set Title

                    findSubfield(v, datafieldPredicateTag200, subfieldPredicateCodeA)
                            .ifPresent(t -> rcDto.setTitle(t.getSubfield()));

                    // Set Contributor

                    rcDto.setCocontributor(IntStream.range(0, datafields.size()).filter(i -> i != posVal - 1)
                            .mapToObj(datafields::get)
                            .map(t -> {
                                List<String> contributor = new ArrayList<>();
                                StringBuilder stringBuilderContributor = getReduceStringBuilderWith2Predicate(
                                        subfieldPredicateCodeA,
                                        subfieldPredicateCodeB,
                                        ", ",t);
                                contributor.add(stringBuilderContributor.toString());
                                return contributor;
                            })
                            .flatMap(List::stream)
                            .collect(Collectors.toList()));

                    // Set publicationDate

                    findSubfield(v, datafieldPredicateTag210, subfieldPredicateCodeD)
                            .ifPresent(t -> rcDto.setPublicationDate(t.getSubfield()));

                    // Set OtherIdDoc

                    rcDto.setOtherIdDoc(v.getDatafieldList().stream().filter(datafieldPredicateTag035)
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeA)
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    // Set DocLang

                    findSubfield(v, datafieldPredicateTag101, subfieldPredicateCodeA)
                            .ifPresent(t -> rcDto.setDocLang(t.getSubfield()));

                    // Set publisher

                    findSubfield(v, datafieldPredicateTag210, subfieldPredicateCodeC)
                            .ifPresent(t -> rcDto.setPublisher(t.getSubfield()));

                    // Set publisherPlace

                    findSubfield(v, datafieldPredicateTag210, subfieldPredicateCodeA)
                            .ifPresent(t -> rcDto.setPublisherPlace(t.getSubfield()));

                    return Mono.just(rcDto);

                }).publishOn(Schedulers.boundedElastic())
                .flatMap(t ->
                        Mono.zip(Mono.just(t),
                            Mono.defer(() -> oracleProxy.getCitation(ppnVal))
                                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                                    .doOnError(e -> log.warn("Can not fetch info from Findra service")),
                            Mono.defer( () -> oracleProxy.getLibCode(t.getRole_code()))
                                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                                    .doOnError(e -> log.warn("Can not fetch info from Findra service")),
                            Mono.defer(() -> oracleProxy.getDomainCode(ppnVal).collectList())
                                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                                    .doOnError(e -> log.warn("Can not fetch info from Findra service"))
                        ).flatMap(v -> {
                            v.getT1().setCitation(v.getT2().getCitation1() + "/" + v.getT2().getCitation3());
                            v.getT1().setRole_fr(v.getT3().getFr());
                            v.getT1().setRole_en(v.getT3().getEn());
                            v.getT1().setDomain_code(v.getT4().stream().map(DomainCode::getCode).collect(Collectors.toList()));
                            v.getT1().setDomain_lib(v.getT4().stream().map(DomainCode::getValeure).collect(Collectors.toList()));
                            return Mono.just(v.getT1());
                }));
                /*.flatMap(t -> Mono.defer(() -> oracleProxy.getCitation(ppnVal))
                        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                        .doOnError(e -> log.warn("Can not fetch info from Findra service")).map(v -> {
                    t.setCitation(v.getCitation1() + "/" +v.getCitation3());
                    return t;
                }))
                .flatMap(t -> Mono.defer( () -> oracleProxy.getLibCode(t.getRole_code()))
                        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                        .doOnError(e -> log.warn("Can not fetch info from Findra service"))
                        .map(v -> {
                    t.setRole_fr(v.getFr());
                    t.setRole_en(v.getEn());
                    return t;
                }))
                .flatMap(t -> Mono.defer(() -> oracleProxy.getDomainCode(ppnVal).collectList())
                        .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                        .doOnError(e -> log.warn("Can not fetch info from Findra service"))
                        .map(v -> {
                    t.setDomain_code(v.stream().map(DomainCode::getCode).collect(Collectors.toList()));
                    t.setDomain_lib(v.stream().map(DomainCode::getValeure).collect(Collectors.toList()));
                    return t;
                }));*/
    }


    private StringBuilder getReduceStringBuilderWith2Predicate(Predicate<Subfield> subfieldPredicateCode1,
                                                               Predicate<Subfield> subfieldPredicateCode2,
                                                               String prefix,
                                                               Datafield t) {
        return t.getSubfieldList().stream()
                .filter(subfieldPredicateCode1.or(subfieldPredicateCode2))
                .map(x -> new StringBuilder(x.getSubfield()))
                .reduce(new StringBuilder(), (a, b) -> {
                    if (a.length() > 0) {
                        a.append(prefix);
                    }
                    a.append(b);
                    return a;
                });
    }

    private StringBuilder getReduceStringBuilderWith3Predicate(
            Predicate<Subfield> subfieldPredicateCode1,
            Predicate<Subfield> subfieldPredicateCode2,
            Predicate<Subfield> subfieldPredicateCode3,
            String prefix,
            Datafield t) {
        return t.getSubfieldList().stream()
                .filter(subfieldPredicateCode1.or(subfieldPredicateCode2).or(subfieldPredicateCode3))
                .map(x -> new StringBuilder(x.getSubfield()))
                .reduce(new StringBuilder(), (a, b) -> {
                    if (a.length() > 0) {
                        a.append(prefix);
                    }
                    a.append(b);
                    return a;
                });
    }

    private Optional<Subfield> findSubfield(XmlRootRecord xmlRootRecord, Predicate<Datafield> datafieldPredicate, Predicate<Subfield> subfieldPredicate) {

        return xmlRootRecord.getDatafieldList().stream().filter(datafieldPredicate)
                .flatMap(t -> t.getSubfieldList().stream())
                .filter(subfieldPredicate)
                .findFirst();
    }


}
