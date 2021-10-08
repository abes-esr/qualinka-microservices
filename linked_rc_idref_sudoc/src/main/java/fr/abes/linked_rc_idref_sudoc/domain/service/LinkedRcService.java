package fr.abes.linked_rc_idref_sudoc.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.abes.linked_rc_idref_sudoc.domain.dto.LinkedRcDto;
import fr.abes.linked_rc_idref_sudoc.domain.dto.LinkedRcGetDto;
import fr.abes.linked_rc_idref_sudoc.domain.entity.ReferenceAutorite;
import fr.abes.linked_rc_idref_sudoc.domain.entity.ReferenceAutoriteFromOracle;
import fr.abes.linked_rc_idref_sudoc.domain.entity.ReferenceContextuelle;
import fr.abes.linked_rc_idref_sudoc.domain.repository.ReferenceAutoriteOracle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
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
public class LinkedRcService {

    private final ReferenceAutoriteOracle referenceAutoriteOracle;
    private final WebClient.Builder webClientBuilder;

    @Value("${solr.base-url}")
    private String solrBaseUrl;

    // This method returns filter function which will log request data
    // Using this for DEBUG mod
    private static ExchangeFilterFunction logRequestWebclient() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    public Mono<LinkedRcGetDto> findLinkedRc(String ra_id) {

        LinkedRcGetDto linkedRc = new LinkedRcGetDto();
        ArrayList<LinkedRcDto> links = new ArrayList<>();
        linkedRc.setQuery("ra_id="+ra_id);

        WebClient webClient = webClientBuilder.baseUrl(this.solrBaseUrl)
                //.filter(logRequestWebclient()) // <== Use this for see WebClient request
                .build();

        ObjectMapper mapper = new ObjectMapper();

        return webClient.get().uri(builder -> builder
                        .path("/solr/sudoc/select")
                        .queryParam("q", "ppn_z:"+ra_id)
                        .queryParam("start", "0")
                        .queryParam("rows", "1")
                        .queryParam("fl", "A200.A200Sb_AS,A200.A200Sa_AS")
                        .queryParam("wt", "json")
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError,
                        response -> Mono.error(
                                new IllegalStateException("Failed to get from the site!")
                        )
                )
                .bodyToMono(JsonNode.class)
                .doOnError(e -> log.error( "ERROR => {}", e.getMessage() ))
                .onErrorResume(e -> Mono.empty())
                .map(jsonNode -> jsonNode.findValue("docs"))
                .map(v -> {
                    ObjectReader reader = mapper.readerFor(new TypeReference<List<ReferenceAutorite>>(){});
                    try {
                        ArrayList<ReferenceAutorite> liste = reader.readValue(v);
                        ReferenceAutorite ref = liste.get(0);

                        linkedRc.setFirstName(ref.getFirstName());
                        linkedRc.setLastName(ref.getLastName());

                        return linkedRc;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return linkedRc;
                    }
                })
                .flatMap(monLinkedRc -> {
                    return webClient.get().uri( uriBuilder -> uriBuilder
                            .path("/solr/sudoc/select")
                            .queryParam("q", "B700.B700S3_BS:" + ra_id + " OR B701.B701S3_BS:" + ra_id + " OR B702.B702S3_BS:" + ra_id)
                            .queryParam("start", "0")
                            .queryParam("rows", "3000")
                            .queryParam("fl", "ppn_z")
                            .queryParam("wt", "json")
                            .build() )
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .onStatus(HttpStatus::isError,
                                    response -> Mono.error(
                                            new IllegalStateException("Failed to get from the site!")
                                    )
                            )
                            .bodyToMono(JsonNode.class)
                            .doOnError(e -> log.error( "ERROR => {}", e.getMessage() ))
                            .onErrorResume(e -> Mono.empty())
                            .map(jsonNode -> jsonNode.findValue("docs"))
                            .map(v -> {
                                ObjectReader reader = mapper.readerFor(new TypeReference<List<ReferenceContextuelle>>(){});
                                try {
                                    return reader.<List<ReferenceContextuelle>>readValue(v);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return new ArrayList<ReferenceContextuelle>();
                                }
                            })
                            .flatMapMany(Flux::fromIterable)
                            .distinct(ReferenceContextuelle::getPpn)
                            .flatMap(x -> referenceContextuelleMonoFromDatabase(x.getPpn(),ra_id))
                            .doOnError(e -> {
                                log.error(e.getLocalizedMessage());
                                e.printStackTrace();
                            })
                            .onErrorResume(v -> Mono.empty())
                            .map(e -> {
                                //System.out.println(e.getPpn());
                                LinkedRcDto link = new LinkedRcDto(e.getPpn());
                                links.add(link);
                                monLinkedRc.setIds(links);
                                monLinkedRc.setCount(links.size());
                                return monLinkedRc;
                            })
                            .last();
                })
                .doOnError(e -> log.warn( "Erreur : " + e.getMessage()))
                .onErrorResume(x -> Mono.just(new LinkedRcGetDto()));
    }

    private Flux<ReferenceContextuelle> referenceContextuelleMonoFromDatabase(String ppn, String ra_id) {
        List<ReferenceContextuelle> referenceContextuelleList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);

        return referenceAutoriteOracle.getEntityWithPos(ppn)
                .doOnError(e -> {
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                })
                .collectSortedList(Comparator.comparing(ReferenceAutoriteFromOracle::getPosfield))
                .map(v ->
                        v.stream().collect(Collectors.groupingBy(ReferenceAutoriteFromOracle::getPosfield))
                                .entrySet().stream().peek(s -> counter.getAndIncrement())
                                .filter(t -> t.getValue().stream().anyMatch(e -> e.getTag().contains("$3")))
                                .reduce(referenceContextuelleList, (s, e) -> {
                                    ReferenceContextuelle referenceContextuelle = new ReferenceContextuelle();
                                    e.getValue().forEach(t -> {
                                        if (t.getTag().contains("$3") && t.getDatas().contains(ra_id)) {
                                            referenceContextuelle.setPpn(t.getPpn() + "-" + counter.get());
                                        }
                                        else {
                                            counter.get();
                                        }
                                    });
                                    if (referenceContextuelle.getPpn()!=null) {
                                        s.add(referenceContextuelle);
                                    }
                                    return s;
                                }, (s1, s2) -> null)
                )
                .flatMapMany(Flux::fromIterable)
                .onErrorResume(v -> Flux.just(new ReferenceContextuelle()));
    }

    //Utility function ( Check doublon with key of Java Stream() )
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
