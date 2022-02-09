package fr.abes.findra.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Strings;
import fr.abes.findra.domain.dto.ReferenceAutoriteDto;
import fr.abes.findra.domain.dto.ReferenceAutoriteGetDto;
import fr.abes.findra.domain.entity.ReferenceAutorite;
import fr.abes.findra.domain.utils.MapStructMapper;
import fr.abes.findra.domain.utils.StringOperator;
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
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReferenceAutoriteService {


    private final WebClient.Builder webClientBuilder;
    private final MapStructMapper mapStructMapper;
    private final StringOperator stringOperator;


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

    public Mono<ReferenceAutoriteGetDto> findAllRA(String from, String fileName, String firstName, String lastName) {


        String flParams = (from != null && from.equals("fromFindrc")) ? "id,ppn_z,B700.B700Sa_BS,B700.B700Sb_BS" : "id,ppn_z,A200.A200Sa_AS,A200.A200Sb_AS";
        List<String> requests = stringOperator.listOfSolrRequestFromPropertieFile(fileName, firstName, lastName);
        WebClient webClient = webClientBuilder.baseUrl(this.solrBaseUrl)
                //.filter(logRequestWebclient()) // <== Use this for see WebClient request
                .build();
        ObjectMapper mapper = new ObjectMapper();

        List<ReferenceAutoriteDto> referenceAutoriteDtoList = new ArrayList<>();
        ReferenceAutoriteGetDto referenceAutoriteGetDto = new ReferenceAutoriteGetDto();
        AtomicInteger ppnCount = new AtomicInteger();

        return Flux.fromIterable(requests)
            .parallel().runOn(Schedulers.boundedElastic())
            .flatMap(x -> webClient.get().uri(builder -> builder
                            .path("/solr/sudoc/select")
                            .queryParam("q", "{requestSolr}")
                            .queryParam("start", "0")
                            .queryParam("rows", "3000")
                            .queryParam("fl", "{flParams}")
                            .queryParam("wt", "json")
                            .build(x, flParams)
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError,
                        response -> Mono.error(
                            new IllegalStateException("Failed to get from the site!")
                        )
                )
                .bodyToMono(JsonNode.class)
                //.timeout(Duration.ofSeconds(30))
                .doOnError(e -> log.error( "ERROR => {}", e.getMessage() ))
                .onErrorResume(e -> Mono.empty())
                .map(jsonNode -> jsonNode.findValue("docs"))
                .map(v -> {
                    ObjectReader reader = mapper.readerFor(new TypeReference<List<ReferenceAutorite>>(){});
                    try {
                        return reader.<List<ReferenceAutorite>>readValue(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ArrayList<ReferenceAutorite>();
                    }
                })
                .flatMapMany(Flux::fromIterable))
                .map(mapStructMapper::referenceAutoriteToreferenceAutoriteDto)
                .sequential()
                .distinct(ReferenceAutoriteDto::getPpn)
                //.filter(x -> ( !Strings.isNullOrEmpty(x.getLastName()) ))
                .filter(x -> {
                    if (!Strings.isNullOrEmpty(x.getFirstName()) && !x.getFirstName().matches("^[a-zA-Z].")
                            && !x.getFirstName().contains(lastName))
                    {

                        return ((( x.getFirstName().split(" ").length >= firstName.split("-").length ) ||
                                ( x.getFirstName().split("-").length >= firstName.split("-").length ) ||
                                ( x.getFirstName().split("\\.").length >= firstName.split("-").length )));

                    }
                    return true;
                    })
                .map(x -> {
                    referenceAutoriteGetDto.setCount(ppnCount.getAndIncrement()+1);
                    referenceAutoriteDtoList.add(x);
                    referenceAutoriteGetDto.setIds(referenceAutoriteDtoList);
                    referenceAutoriteGetDto.addQuery(firstName, lastName);
                    referenceAutoriteGetDto.setQueries(fileName);
                    return referenceAutoriteGetDto;
                })
            .last()
            .doOnError(e -> log.warn( "Name not found or failed to parsing JSON" ))
            .onErrorResume(x -> Mono.just(new ReferenceAutoriteGetDto(0, null, fileName, new ArrayList<>())));
    }


    //Utility function ( Check doublon with key of Java Stream() )
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;

    }
}
