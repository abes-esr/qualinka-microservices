package fr.abes.findrc.domain.service;

import com.google.common.base.Strings;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDto;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoProxy;
import fr.abes.findrc.domain.dto.ReferenceAutoriteGetDto;
import fr.abes.findrc.domain.entity.ReferenceAutoriteFromOracle;
import fr.abes.findrc.domain.repository.ReferenceAutoriteOracle;
import fr.abes.findrc.domain.repository.ReferenceAutoriteProxy;
import fr.abes.findrc.domain.utils.LuceneSearch;
import fr.abes.findrc.domain.utils.MapStructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.validation.constraints.AssertFalse;
import java.text.Normalizer;
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

    public Mono<ReferenceAutoriteGetDto> findAllRCFromDatabase(String fileName, String firstName, String lastName) {

        List<ReferenceAutoriteDto> referenceAutoriteDtoList = new ArrayList<>();
        ReferenceAutoriteGetDto referenceAutoriteGetDto = new ReferenceAutoriteGetDto();
        referenceAutoriteGetDto.setCount(0);
        referenceAutoriteGetDto.setIds(new ArrayList<>());
        referenceAutoriteGetDto.setQueries(fileName);
        referenceAutoriteGetDto.addQuery(firstName,lastName);
        AtomicInteger ppnCount = new AtomicInteger(1);
        String finalLastName = Normalizer.normalize(lastName, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        Mono<ReferenceAutoriteDtoProxy> referenceAutoriteDtoProxyMono = Mono.defer(() -> referenceAutoriteProxy.findraExchangeProxy("fromFindrc", fileName, firstName, lastName))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .doOnError(e -> log.warn("Can not fetch info from Findra service"))
                .onErrorResume(v -> Mono.empty());

        return referenceAutoriteDtoProxyMono
                //.map(mapStructMapper::ReferenceProxyToDto)
                .map(ReferenceAutoriteDtoProxy::getIds)
                .flatMapMany(Flux::fromIterable)
                .flatMap(x -> referenceAutoriteMonoFromDatabase(x.getPpn()))
                .filter(t -> {
                    try {
                        //ACT 27/06/24 : on accepte * en firstName
                        //if (!Strings.isNullOrEmpty(t.getFirstName())) {

                        if (!Strings.isNullOrEmpty(t.getFirstName()) && firstName.charAt(0) != '*') {

                            return
                                !Strings.isNullOrEmpty(t.getLastName())
                                &&
                                (LuceneSearch.Search(t.getLastName(), finalLastName.replace("-", " ") + "~0.8") > 0)
                                &&
                                (
                                    LuceneSearch.Search(t.getFirstName(), firstName.replace("-", " ") + "~0.8") > 0
                                    || LuceneSearch.Search(t.getFirstName(), firstName.charAt(0) + "*" + "~0.8") > 0
                                    || (firstName.replace(".", "").length() == 1 && t.getFirstName().equals(firstName))
                                );
                        } else {
                            return
                                !Strings.isNullOrEmpty(t.getLastName())
                                &&
                                (LuceneSearch.Search(t.getLastName(), finalLastName.replace("-", " ") + "~0.8") > 0);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .map(e -> {
                    //System.out.println(e.getPpn() + ":" + e.getFirstName() + ":" + e.getLastName());
                    referenceAutoriteDtoList.add(e);
                    referenceAutoriteGetDto.setIds(referenceAutoriteDtoList);
                    referenceAutoriteGetDto.setQueries(fileName);
                    referenceAutoriteGetDto.setCount(ppnCount.getAndIncrement());
                    return referenceAutoriteGetDto;
                })
                .last()
                .onErrorResume( t -> Mono.empty() )
                .doOnError(e -> {
                    log.error( "ERROR => {}", e.getMessage() );
                })
                .switchIfEmpty(Mono.just(referenceAutoriteGetDto));
    }

    private Flux<ReferenceAutoriteDto> referenceAutoriteMonoFromDatabase(String ppn) {

        List<ReferenceAutoriteDto> referenceAutoriteList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        return referenceAutoriteOracle.getEntityWithPos(ppn)
                .collectList()
                .map(v ->
                        v.stream()
                                .collect(Collectors.groupingBy(ReferenceAutoriteFromOracle::posfield))
                                .entrySet().stream()
                                .sorted(Comparator.comparingInt(t -> Integer.parseInt(t.getKey())))
                                .peek(s -> counter.getAndIncrement())
                                .filter(t -> t.getValue().stream().noneMatch(e -> e.tag().contains("$3")) &&
                                        t.getValue().stream().noneMatch(e -> e.tag().contains("$1")) &&
                                        t.getValue().stream().noneMatch(e -> e.tag().contains("$5"))
                                )
                                .reduce(referenceAutoriteList, (s, e) -> {

                                    ReferenceAutoriteDto referenceAutorite = new ReferenceAutoriteDto();
                                    s.add(e.getValue().stream().map(t -> {
                                        referenceAutorite.setPpn(t.ppn() + "-" + counter.get());
                                        if (t.tag().contains("$a")) {
                                            referenceAutorite.setLastName(t.datas());
                                        }
                                        if (t.tag().contains("$b")) {
                                            referenceAutorite.setFirstName(t.datas());
                                        }
                                        return referenceAutorite;
                                    })
                                            .collect(Collectors.toList())
                                            .get(0));
                                    return s;
                                }, (s1, s2) -> null)
                )
                .flatMapMany(Flux::fromIterable)
                .onErrorResume( t -> Mono.error( new IllegalStateException( "Error connecting Database" ) ) )
                .doOnError(e -> {
                    log.error( "ERROR => {}", e.getMessage() );
                });
    }

    //Utility function ( Check doublon with key of Java Stream() )
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;

    }
}
