package fr.abes.findrc.domain.service;

import com.google.common.base.Strings;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDto;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoProxy;
import fr.abes.findrc.domain.entity.ReferenceAutorite;
import fr.abes.findrc.domain.entity.ReferenceAutoriteFromOracle;
import fr.abes.findrc.domain.entity.XmlRootRecord;
import fr.abes.findrc.domain.repository.ReferenceAutoriteOracle;
import fr.abes.findrc.domain.repository.ReferenceAutoriteProxy;
import fr.abes.findrc.domain.utils.LuceneSearch;
import fr.abes.findrc.domain.utils.MapStructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReferenceContextuelService {

    private final ReferenceAutoriteOracle referenceAutoriteOracle;
    private final WebClient.Builder webClientBuilder;
    private final ReferenceAutoriteProxy referenceAutoriteProxy;
    private final MapStructMapper mapStructMapper;


    // This method returns filter function which will log request data
    // Using this for DEBUG mod
    private static ExchangeFilterFunction logRequestWebclient() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    public Mono<ReferenceAutoriteDto> findAllRCFromDatabase(String fileName, String firstName, String lastName) {

        List<ReferenceAutorite> referenceAutoriteDtoList = new ArrayList<>();
        ReferenceAutoriteDto referenceAutoriteGetDto = new ReferenceAutoriteDto();
        AtomicInteger ppnCount = new AtomicInteger();

        return Mono.defer(() -> referenceAutoriteProxy.findraExchangeProxy("fromFindrc", fileName, firstName, lastName))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .doOnError(e -> log.warn("Can not fetch info from Findra service"))
                .onErrorResume(v -> Mono.empty())
                .map(mapStructMapper::ReferenceProxyToDto)
                .flatMapMany(v -> Flux.fromIterable(v.getReferenceContextuel()))
                .distinct(ReferenceAutorite::getPpn)
                .flatMap(x -> referenceAutoriteMonoFromDatabase(x.getPpn()))
                .doOnError(e -> {
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                })
                .filter(x -> {
                    try {
                        return
                            !Strings.isNullOrEmpty(x.getLastName())
                            &&
                            (LuceneSearch.Search(x.getLastName(), lastName.replace("-", " ") + "~0.8") > 0)
                            &&
                            (
                            LuceneSearch.Search(x.getFirstName(), firstName.replace("-", " ") + "~0.8") > 0
                            || LuceneSearch.Search(x.getFirstName(), firstName.charAt(0) + "*" + "~0.8") > 0
                            || (firstName.replace(".", "").length() == 1 && x.getFirstName().equals(firstName))
                            );
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .onErrorResume(v -> Mono.empty())
                .map(e -> {
                    //System.out.println(e.getPpn() + ":" + e.getFirstName() + ":" + e.getLastName());
                    referenceAutoriteDtoList.add(e);
                    referenceAutoriteGetDto.setReferenceContextuel(referenceAutoriteDtoList);
                    referenceAutoriteGetDto.setPpnCounter(ppnCount.getAndIncrement() + 1);

                    return referenceAutoriteGetDto;
                })
                .last()
                .doOnError(e -> {
                    log.warn("Name not found");
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                })
                .onErrorResume(x -> Mono.just(new ReferenceAutoriteDto(0, new ArrayList<>())));
    }

    /*public Mono<ReferenceAutoriteDto> findAllRC(String fileName, String firstName, String lastName) {

        List<ReferenceAutorite> referenceAutoriteDtoList = new ArrayList<>();
        ReferenceAutoriteDto referenceAutoriteGetDto = new ReferenceAutoriteDto();
        AtomicInteger ppnCount = new AtomicInteger();

        return Mono.defer(() -> referenceAutoriteProxy.findraExchangeProxy("fromFindrc", fileName, firstName, lastName))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .doOnError(e -> log.warn("Can not fetch info from Findra service"))
                .onErrorResume(v -> Mono.empty())
                .map(mapStructMapper::ReferenceProxyToDto)
                .flatMapMany(v -> Flux.fromIterable(v.getReferenceContextuel()))
                .distinct(ReferenceAutorite::getPpn)
                .flatMap(x -> referenceAutoriteMono(x.getPpn()))
                .filter(x -> {
                    try {
                        return
                            !Strings.isNullOrEmpty(x.getLastName())
                            &&
                            (LuceneSearch.Search(x.getLastName(), lastName.replace("-", " ") + "~0.8") > 0)
                            &&
                            (
                            LuceneSearch.Search(x.getFirstName(), firstName.replace("-", " ") + "~0.8") > 0
                            || LuceneSearch.Search(x.getFirstName(), firstName.charAt(0) + "*" + "~0.8") > 0
                            || (firstName.replace(".", "").length() == 1 && x.getFirstName().equals(firstName))
                            );
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .onErrorResume(v -> Mono.empty())
                .map(e -> {
                    //System.out.println(e.getPpn() + ":" + e.getFirstName() + ":" + e.getLastName());
                    referenceAutoriteDtoList.add(e);
                    referenceAutoriteGetDto.setReferenceContextuel(referenceAutoriteDtoList);
                    referenceAutoriteGetDto.setPpnCounter(ppnCount.getAndIncrement() + 1);

                    return referenceAutoriteGetDto;
                })
                .last()
                .doOnError(e -> log.warn("Name not found or failed to parsing XML from SUDOC Server"))
                .onErrorResume(x -> Mono.just(new ReferenceAutoriteDto(0, new ArrayList<>())));
    }*/

    private Flux<ReferenceAutorite> referenceAutoriteMono(String ppn) {

        AtomicInteger counter = new AtomicInteger(0);
        WebClient webClient = webClientBuilder.baseUrl("https://www.sudoc.fr/").build();

        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(ppn + ".abes")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(XmlRootRecord.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .doOnError(v -> log.error("ERROR => {}", v.getMessage()))
                .onErrorResume(v -> Mono.empty())
                .flatMapIterable(XmlRootRecord::getDatafieldList)
                .filter(v -> v.getTag().startsWith("70"))
                .doOnEach(v -> counter.getAndIncrement())
                .filter(v -> v.getSubfieldList()
                        .stream()
                        .noneMatch(t -> t.getCode().equals("3"))
                )
                .map(v -> {
                //log.info("Execute by Thread : " + Thread.currentThread().getName());
                /*System.out.println("TAG = " + v.getTag());
                System.out.println("PPN = " + ppn);
                System.out.println("POS = " + counter.get());*/
                    ReferenceAutorite referenceAutoriteDto = new ReferenceAutorite();
                    v.getSubfieldList().forEach(t -> {

                            //System.out.println(t.getCode() + " : " + t.getSubfield());
                            referenceAutoriteDto.setPpn(ppn + "-" + counter.get());
                            if (t.getCode().equals("a")) {
                                referenceAutoriteDto.setLastName(t.getSubfield());
                            }
                            if (t.getCode().equals("b")) {
                                referenceAutoriteDto.setFirstName(t.getSubfield());
                            }
                        }
                    );
                    /*System.out.println("==================================");*/
                    return referenceAutoriteDto;
                })
                .doOnEach(v -> counter.set(0))
                .onErrorResume(v -> Flux.just(new ReferenceAutorite()));
    }

    private Flux<ReferenceAutorite> referenceAutoriteMonoFromDatabase(String ppn) {

        List<ReferenceAutorite> referenceAutoriteList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        return referenceAutoriteOracle.getEntityWithPos(ppn)
                .doOnError(e -> {
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                })
                .collectSortedList(Comparator.comparing(ReferenceAutoriteFromOracle::posfield))
                .map(v ->
                        v.stream().collect(Collectors.groupingBy(ReferenceAutoriteFromOracle::posfield))
                                .entrySet().stream().peek(s -> counter.getAndIncrement())
                                .filter(t -> t.getValue().stream().noneMatch(e -> e.tag().contains("$3")))
                                .reduce(referenceAutoriteList, (s, e) -> {
                                    ReferenceAutorite referenceAutorite = new ReferenceAutorite();
                                    e.getValue().forEach(t -> {
                                        referenceAutorite.setPpn(t.ppn() + "-" + counter.get());
                                        if (t.tag().contains("$a")) {
                                            referenceAutorite.setLastName(t.datas());
                                        }
                                        if (t.tag().contains("$b")) {
                                            referenceAutorite.setFirstName(t.datas());
                                        }
                                    });
                                    s.add(referenceAutorite);
                                    return s;
                                }, (s1, s2) -> null)
                )
                .flatMapMany(Flux::fromIterable)
                .onErrorResume(v -> Flux.just(new ReferenceAutorite()));
    }

    //Utility function ( Check doublon with key of Java Stream() )
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;

    }
}
