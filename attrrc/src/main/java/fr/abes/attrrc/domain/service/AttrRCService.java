package fr.abes.attrrc.domain.service;

import fr.abes.attrrc.domain.dto.DomainCodeDto;
import fr.abes.attrrc.domain.dto.RCDto;
import fr.abes.attrrc.domain.entity.*;
import fr.abes.attrrc.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Slf4j
@Service
public class AttrRCService {

    private final OracleReferenceAuth oracleReferenceAuth;

    // This method returns filter function which will log request data
    // Using this for DEBUG mod
    private static ExchangeFilterFunction logRequestWebclient() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    public Mono<RCDto> attributs(String rc_id) throws SQLException {


        RCDto rcDto = new RCDto();
        String ppnVal = rc_id.substring(0, rc_id.indexOf("-"));
        int posVal = Integer.parseInt(rc_id.substring(rc_id.indexOf("-") + 1));

        return oracleReferenceAuth.getXmlRootRecordOracle(ppnVal)
                .flatMap(v -> {

                    Predicate<Datafield> datafieldPredicateTag035 = t -> t.getTag().equals("035");
                    Predicate<Datafield> datafieldPredicateTag100 = t -> t.getTag().equals("100");
                    Predicate<Datafield> datafieldPredicateTag101 = t -> t.getTag().equals("101");
                    Predicate<Datafield> datafieldPredicateTag105 = t -> t.getTag().equals("105");
                    Predicate<Datafield> datafieldPredicateTag200 = t -> t.getTag().equals("200");
                    Predicate<Datafield> datafieldPredicateTag210 = t -> t.getTag().equals("210");
                    Predicate<Datafield> datafieldPredicateTag214 = t -> t.getTag().equals("214");
                    Predicate<Datafield> datafieldPredicateTag600 = t -> t.getTag().equals("600");
                    Predicate<Datafield> datafieldPredicateTag601 = t -> t.getTag().equals("601");
                    Predicate<Datafield> datafieldPredicateTag602 = t -> t.getTag().equals("602");
                    Predicate<Datafield> datafieldPredicateTag604 = t -> t.getTag().equals("604");
                    Predicate<Datafield> datafieldPredicateTag605 = t -> t.getTag().equals("605");
                    Predicate<Datafield> datafieldPredicateTag606 = t -> t.getTag().equals("606");
                    Predicate<Datafield> datafieldPredicateTag607 = t -> t.getTag().equals("607");
                    Predicate<Datafield> datafieldPredicateTag608 = t -> t.getTag().equals("608");
                    Predicate<Datafield> datafieldPredicateTag676 = t -> t.getTag().equals("676");
                    Predicate<Datafield> datafieldPredicateTag71 = t -> t.getTag().startsWith("71");
                    Predicate<Subfield> subfieldPredicateCode2 = t -> t.getCode().equals("2");
                    Predicate<Subfield> subfieldPredicateCode3 = t -> t.getCode().equals("3");
                    Predicate<Subfield> subfieldPredicateCode4 = t -> t.getCode().equals("4");
                    Predicate<Subfield> subfieldPredicateCodeA = t -> t.getCode().equals("a");
                    Predicate<Subfield> subfieldPredicateCodeB = t -> t.getCode().equals("b");
                    Predicate<Subfield> subfieldPredicateCodeC = t -> t.getCode().equals("c");
                    Predicate<Subfield> subfieldPredicateCodeD = t -> t.getCode().equals("d");
                    Predicate<Subfield> subfieldPredicateCodeE = t -> t.getCode().equals("e");
                    Predicate<Subfield> subfieldPredicateNotCode2 = t -> !t.getCode().equals("2");
                    Predicate<Subfield> subfieldPredicateNotCode3 = t -> !t.getCode().equals("3");



                    //Set ID
                    rcDto.setId(rc_id);

                    // Set dateCreationNotice
                    v.getControlfieldList()
                            .stream()
                            .filter(t -> t.getTag().equals("004"))
                            .findFirst()
                            .ifPresent(t -> rcDto.setDateCreationNotice(t.getControlfield()));

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
                                    a.append(", ");
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
                                        List<Subfield> subfieldsList = t.getSubfieldList().stream().filter(u -> (u.getCode().equals("2") && u.getSubfield().contains("rameau"))).collect(Collectors.toList());
                                        if (subfieldsList.size() > 0) {
                                            StringBuilder stringBuilderRameau = t.getSubfieldList().stream()
                                                    .filter(u -> !(u.getCode().equals("2") || u.getCode().equals("3")))
                                                    .map(x -> new StringBuilder(x.getSubfield()))
                                                    .reduce(new StringBuilder(), (a, b) -> {
                                                        if (a.length() > 0) {
                                                            a.append(" -- ");
                                                        }
                                                        a.append(b);
                                                        return a;
                                                    });
                                            rameau.add(stringBuilderRameau.toString());
                                        }
                                        return rameau;
                                    })
                                    .flatMap(List::stream)
                                    .collect(Collectors.toList())
                    );

                    // Set Mesh
                    List<Datafield> datafieldsMesh = v.getDatafieldList().stream()
                            .filter(datafieldPredicateTag606).collect(Collectors.toList());

                    rcDto.setMesh(datafieldsMesh.stream().map(t -> {
                                        List<String> mesh = new ArrayList<>();
                                        List<Subfield> subfieldsList = t.getSubfieldList().stream().filter(u -> (u.getCode().equals("2") && u.getSubfield().contains("esh"))).collect(Collectors.toList());
                                        if (subfieldsList.size() > 0) {
                                            StringBuilder stringBuilderRameau = t.getSubfieldList().stream()
                                                    .filter(u -> !(u.getCode().equals("2") || u.getCode().equals("3")))
                                                    .map(x -> new StringBuilder(x.getSubfield()))
                                                    .reduce(new StringBuilder(), (a, b) -> {
                                                        if (a.length() > 0) {
                                                            a.append(" -- ");
                                                        }
                                                        a.append(b);
                                                        return a;
                                                    });
                                            mesh.add(stringBuilderRameau.toString());
                                        }
                                        return mesh;
                                    })
                                    .flatMap(List::stream)
                                    .collect(Collectors.toList())
                    );

                    // Set 600 new
                    List<Datafield> datafields600new = v.getDatafieldList().stream()
                                                        .filter(datafieldPredicateTag600)
                                                        .collect(Collectors.toList());
                    AtomicInteger j = new AtomicInteger(0);

                    rcDto.setSubject600new(
                            datafields600new.stream().map(t -> {
                                List<String> subfield600new = new ArrayList<>();
                                List<Subfield> subfieldsList = t.getSubfieldList().stream().filter(subfieldPredicateCode3).collect(Collectors.toList());
                                if (subfieldsList.size() > 0) {
                                    StringBuilder stringBuilder600new = t.getSubfieldList().stream()
                                            .filter(f -> !(f.getCode().equals("2") || f.getCode().equals("3")))
                                            .map(x -> new StringBuilder(x.getSubfield()))
                                            .reduce(new StringBuilder(), (a, b) -> {
                                                if (j.get()==1) {
                                                    a.append(" (");
                                                }
                                                a.append(b);
                                                j.getAndIncrement();
                                                return a;
                                            });
                                    subfield600new.add(stringBuilder600new.append(")").toString());
                                }
                                return subfield600new;
                            })
                            .flatMap(List::stream)
                            .collect(Collectors.toList()));

                    // Set 600
                    rcDto.setSubject600(getList60X(v, datafieldPredicateTag600, subfieldPredicateNotCode3));

                    // Set 601
                    rcDto.setSubject601(getList60X(v, datafieldPredicateTag601, subfieldPredicateNotCode3));

                    // Set 602
                    rcDto.setSubject602(getList60X(v,datafieldPredicateTag602, subfieldPredicateNotCode3));

                    // Set 604
                    rcDto.setSubject604(getList60X(v,datafieldPredicateTag604, subfieldPredicateNotCode3));

                    // Set 605
                    rcDto.setSubject605(getList60X(v,datafieldPredicateTag605, subfieldPredicateNotCode3));

                    // Set 607
                    rcDto.setSubject607(getList60X(v, datafieldPredicateTag607, subfieldPredicateNotCode2, subfieldPredicateNotCode3));

                    // Set 608
                    rcDto.setSubject608(getList60X(v, datafieldPredicateTag608, subfieldPredicateNotCode2, subfieldPredicateNotCode3));

                    // Set Set dewey
                    rcDto.setDewey(v.getDatafieldList().stream().filter(datafieldPredicateTag676)
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeA)
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    // Set Title
                    rcDto.setTitle(removedUnicode989C(v.getDatafieldList().stream().filter(datafieldPredicateTag200)
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeA.or(subfieldPredicateCodeE))
                            .map(t -> new StringBuilder(t.getSubfield()))
                            .reduce(new StringBuilder(), (a, b) -> {
                                if (a.length() > 0) {
                                    a.append(" : ");
                                }
                                a.append(b);
                                return a;
                            }).toString()));

                    // Set Contributor
                    rcDto.setCocontributor(IntStream.range(0, datafields.size()).filter(i -> i != posVal - 1)
                            .mapToObj(datafields::get)
                            .map(t -> {
                                List<String> contributor = new ArrayList<>();
                                StringBuilder stringBuilderContributor = getReduceStringBuilderWithPredicate(
                                        ", ",t,subfieldPredicateCodeA,subfieldPredicateCodeB);
                                contributor.add(stringBuilderContributor.toString());
                                return contributor;
                            })
                            .flatMap(List::stream)
                            .collect(Collectors.toList()));

                    // Set CorporateBody
                    List<Datafield> datafields71 = v.getDatafieldList().stream()
                            .filter(datafieldPredicateTag71).collect(Collectors.toList());

                    rcDto.setCorporateBody(datafields71.stream().map(t -> {
                                List<String> corporate = new ArrayList<>();
                                StringBuilder stringBuilderCorporate = getReduceStringBuilderWithPredicate(
                                        ", ",t,subfieldPredicateCodeA,subfieldPredicateCodeB);
                                corporate.add(stringBuilderCorporate.toString());
                                return corporate;
                            })
                            .flatMap(List::stream)
                            .collect(Collectors.toList()));



                    findSubfield(v, datafieldPredicateTag100, subfieldPredicateCodeA)
                            .ifPresent(t -> {
                                if (t.getSubfield().length()>17){
                                    // Set publicationDate
                                    if (t.getSubfield().charAt(8) == 'd') {
                                        rcDto.setPublicationDate(t.getSubfield().substring(10,14));
                                    }else {
                                        findSubfield(v, datafieldPredicateTag210, subfieldPredicateCodeD)
                                                .ifPresentOrElse(
                                                        p -> rcDto.setPublicationDate(p.getSubfield()),
                                                        () ->
                                                        findSubfield(v, datafieldPredicateTag214, subfieldPredicateCodeD)
                                                                .ifPresent(n -> rcDto.setPublicationDate(n.getSubfield()))
                                                        );

                                    }
                                    // Set originalPublicationDate
                                    if (t.getSubfield().charAt(8) == 'e') {
                                        rcDto.setOriginalPublicationDate(t.getSubfield().substring(13,17));
                                    }
                                }
                            });


                    // Set OtherIdDoc
                    rcDto.setOtherIdDoc(v.getDatafieldList().stream().filter(datafieldPredicateTag035)
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeA)
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    // Set DocLang
                    rcDto.setDocLang(v.getDatafieldList().stream().filter(datafieldPredicateTag101)
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeA)
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    // Set OriginalDocLang
                    rcDto.setOriginalDocLang(v.getDatafieldList().stream().filter(datafieldPredicateTag101)
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeC)
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    // Set publisher
                    rcDto.setPublisher(v.getDatafieldList().stream().filter(datafieldPredicateTag210.or(datafieldPredicateTag214))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeC)
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    // Set publisherPlace
                    rcDto.setPublisherPlace(v.getDatafieldList().stream().filter(datafieldPredicateTag210.or(datafieldPredicateTag214))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(subfieldPredicateCodeA)
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    // Set genre TODO : à tester
                    findSubfield(v, datafieldPredicateTag105, subfieldPredicateCodeA)
                            .ifPresent(t -> {
                                if (t.getSubfield().length()>13){
                                    rcDto.setGenre(t.getSubfield().substring(12));
                                }
                            });

                    return Mono.just(rcDto);

                })
                .flatMap(v -> oracleReferenceAuth.getCitationOracle(ppnVal)
                        .map(t -> {
                            v.setCitation(removedUnicode989C(t.citation1() + " / " + t.citation3()));
                            return v;
                        }))
                .flatMap(v -> oracleReferenceAuth.getDomainCodeAndValue(ppnVal).collectList()
                        .map(t -> {
                            v.setDomain_code(t.stream().map(DomainCodeDto::code).collect(Collectors.toList()));
                            v.setDomain_lib(t.stream().map(DomainCodeDto::valeure).collect(Collectors.toList()));
                            return v;
                        }))
                .flatMap(v -> oracleReferenceAuth.getLibRoleOracle(v.getRole_code())
                        .map(t -> {
                            v.setRole_fr(t.fr());
                            v.setRole_en(t.en());
                            return v;
                        }))
                .flatMap(v -> oracleReferenceAuth.getkeywordOracle(ppnVal).collectList()
                        .map(t -> {
                            List<String> keywordsList = t.stream().map(e -> e.split("[,:;/.]"))
                                    .flatMap(Stream::of)
                                    .map(String::trim)
                                    .collect(Collectors.toList());
                            v.setKeyword(keywordsList);
                            return v;
                        }))
                .doOnError(e -> log.warn("Not found resultat from SQL with the PPN {}", ppnVal))
                .onErrorResume(t -> Mono.empty())
                .switchIfEmpty(Mono.just(rcDto));

    }

    // Traitement les Datafield avec les tag 60x
    // Capable de gérer Multi filters ( une liste de predicates ) avec OR NOT
    // ex : (t -> !(t.getCode().equals("2") || t.getCode().equals("3"))
    @SafeVarargs
    private List<String> getList60X(XmlRootRecord v,
                                    Predicate<Datafield> datafieldPredicateTag60X,
                                    Predicate<Subfield>... predicates) {
        List<Datafield> datafields60X = v.getDatafieldList().stream()
                .filter(datafieldPredicateTag60X).collect(Collectors.toList());

        return datafields60X.stream().map(t -> {
                    List<String> subfield60X = new ArrayList<>();
                    StringBuilder stringBuilder60X = t.getSubfieldList().stream()
                            .filter(combineFiltersWithAnd(predicates))
                            .map(x -> new StringBuilder(x.getSubfield()))
                            .reduce(new StringBuilder(), (a, b) -> {
                                if (a.length() > 0) {
                                    a.append(", ");
                                }
                                a.append(b);
                                return a;
                            });

                    subfield60X.add(stringBuilder60X.toString());
                    return subfield60X;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    // Capable de gérer Multi filters ( une liste de predicates ) avec OR
    // ex : (t -> (t.getCode().equals("2") || t.getCode().equals("3"))
    @SafeVarargs
    private StringBuilder getReduceStringBuilderWithPredicate(String prefix,
                                                              Datafield t,
                                                              Predicate<Subfield>... predicates) {
        return t.getSubfieldList().stream()
                .filter(combineFiltersWithOr(predicates))
                .map(x -> new StringBuilder(x.getSubfield()))
                .reduce(new StringBuilder(), (a, b) -> {
                    if (a.length() > 0) {
                        a.append(prefix);
                    }
                    a.append(b);
                    return a;
                });
    }


    private Optional<Subfield> findSubfield(XmlRootRecord xmlRootRecord,
                                            Predicate<Datafield> datafieldPredicate,
                                            Predicate<Subfield> subfieldPredicate) {

        return xmlRootRecord.getDatafieldList().stream().filter(datafieldPredicate)
                .flatMap(t -> t.getSubfieldList().stream())
                .filter(subfieldPredicate)
                .findFirst();
    }

    /**
     * Removed unicode 989 C.
     *
     * @param s the s
     * @return the string
     */
    public static String removedUnicode989C(String s) {
        String[] splitted = s.split("[" + '\u0098' + '\u009C' + "]");

        // Si c'est plus grand que 3, c'est normal (un délimitateur par notice)
        int l = splitted.length;
        if (l > 3){
            return s;
        }
        // Cleaned text() without Unicode \U0098, \U009C
        StringBuilder builder = new StringBuilder(splitted[0]);
        for (int i = 1; i < l; i++)
            builder.append(splitted[i]);

        return builder.toString();
    }


    @SafeVarargs
    private static <T> Predicate<T> combineFiltersWithOr(Predicate<T>... predicates) {

        return Stream.of(predicates).reduce(Predicate::or).orElse(x->false);
    }

    @SafeVarargs
    private static <T> Predicate<T> combineFiltersWithAnd(Predicate<T>... predicates) {

        return Stream.of(predicates).reduce(Predicate::and).orElse(x->false);
    }





}
