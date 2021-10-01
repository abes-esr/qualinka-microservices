package fr.abes.partitionInitiale.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Strings;
import fr.abes.partitionInitiale.domain.dto.PartitionInitialeDto;
import fr.abes.partitionInitiale.domain.dto.PartitionInitialeLink;
import fr.abes.partitionInitiale.domain.dto.ReferenceAutoriteDto;
import fr.abes.partitionInitiale.domain.entity.ReferenceAutorite;
import fr.abes.partitionInitiale.domain.dto.ReferenceAutoriteDtoProxy;
import fr.abes.partitionInitiale.domain.entity.Resu;
import fr.abes.partitionInitiale.domain.entity.XmlRootRecord;
import fr.abes.partitionInitiale.domain.repository.ReferenceAutoriteProxy;

import fr.abes.partitionInitiale.domain.repository.ReferenceContextuelProxy;
import fr.abes.partitionInitiale.domain.utils.MapStructMapper;
import io.netty.channel.ChannelException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.TimeoutException;
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

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
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
public class PartitionInitialeService {


    private final WebClient.Builder webClientBuilder;
    private final ReferenceAutoriteProxy referenceAutoriteProxy;
    private final ReferenceContextuelProxy referenceContextuelProxy;
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

    public Mono<PartitionInitialeDto> findAllRC(String firstName, String lastName) {

        PartitionInitialeDto partitionInitialeDto = new PartitionInitialeDto();
        ArrayList<String> targets = new ArrayList<>();
        ArrayList<PartitionInitialeLink> initialLinks = new ArrayList<>();
        ArrayList<String> sources = new ArrayList<>();

        WebClient webClient = webClientBuilder.baseUrl("https://www-test.sudoc.fr/services/generic/").build();

        AtomicInteger ppnCount = new AtomicInteger();

        ObjectMapper mapper = new ObjectMapper();

        return referenceAutoriteProxy.findraExchangeProxy(firstName, lastName)
              .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
              .doOnError(e -> log.warn( "Can not fetch info from Findra service" ))
              .onErrorResume(v -> Mono.empty())
                //.map(mapStructMapper::ReferenceProxyToDto)
                .flatMapMany(v -> Flux.fromIterable(v.getReferenceAutorite()))
                .distinct(ReferenceAutorite::getPpn)
                .flatMap(f -> {
                    //On a les targets, avec ca
                    //System.out.println("==>" + f.getReferenceAutorite());
                    targets.add("idref:"+f.getPpn());

                    partitionInitialeDto.setTargets(targets);

                    /*Mono<String> response = webClient.get().uri( uriBuilder -> uriBuilder
                            .queryParam("servicekey", "qualinca_ppnbib_pos")
                            .queryParam("format", "text/json")
                            .queryParam("ppnaut", f.getPpn().replace("idref:",""))
                            .build() )
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(String.class);

                        response.subscribe(
                                value -> {
                                    try {
                                        System.out.println("HELLO" + ((JsonNode) mapper.readValue(value, JsonNode.class)).findValue("ppns").size());
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }
                                },
                                error -> error.printStackTrace(),
                                () -> System.out.println("completed without a value")
                        );*/

                    /*ObjectReader reader = mapper.readerFor(new TypeReference<List<Resu>>(){});
                    try {
                         ArrayList<Resu> resu = reader.<ArrayList<Resu>>readValue(((JsonNode)
                                 mapper.readValue("{\"ppns\":[{\"resu\":{\"content\":null,\"PPNBIBLIO\":\"229023274-1\",\"CODE\":\"070\",\"ROLE\":\"Auteur\"}}]}", JsonNode.class)).findValue("ppns"));
                        System.out.println("oooo>"+resu.get(0).getResu().getPpnbiblio());
                    } catch (IOException e) {
                        System.out.println("Error:"+e.getMessage());
                    }*/

                    //Avec ppn_bib_pos on a les sources liées aux targets
                   return webClient.get().uri( uriBuilder -> uriBuilder
                            .queryParam("servicekey", "qualinca_ppnbib_pos")
                            .queryParam("format", "text/json")
                            .queryParam("ppnaut", f.getPpn().replace("idref:",""))
                            .build() )
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .bodyToMono(String.class).map(jsonNode -> {
                                    try {
                                        //System.out.println("XX==>"+jsonNode);
                                        return ((JsonNode) mapper.readValue(jsonNode, JsonNode.class)).findValue("ppns");
                                    } catch (JsonProcessingException e) {
                                        System.out.println("Exception:"+e.getMessage());
                                        return mapper.createObjectNode();
                                    }
                            })
                            .map(v -> {
                                //System.out.println("====>"+v.size());
                                ObjectReader reader = mapper.readerFor(new TypeReference<List<Resu>>(){});
                                try {
                                    return reader.<List<Resu>>readValue(v);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    System.out.println("ERror : "+e.getMessage());
                                    return new ArrayList<Resu>();
                                }
                            })
                            .flatMapMany(Flux::fromIterable)
                            .map(t -> {
                                //System.out.println("t.getResu().getPPNBIBLIO() "+t.getResu().getPpnbiblio());
                                //t.getResu().getPPNBIBLIO()
                                initialLinks.add(new PartitionInitialeLink("sudoc:"+t.getResu().getPpnbiblio(),"sameAs",f.getPpn()));
                                sources.add("sudoc:"+t.getResu().getPpnbiblio());
                                partitionInitialeDto.setSources(sources);
                                partitionInitialeDto.setInitialLinks(initialLinks);
                                return partitionInitialeDto;
                            }).last();
                }
                )
                .last()
                .flatMap( t -> {
                        return referenceContextuelProxy.findrcExchangeProxy(firstName, lastName)
                                .flatMapMany(v -> Flux.fromIterable(v.getReferenceAutorite()))
                                .distinct(ReferenceAutorite::getPpn)
                                .map(f -> {
                                    //System.out.println("FindRC : "+f.getPpn());
                                    //On a les sources non liées, avec find2 (findRC)
                                    sources.add("sudoc:"+f.getPpn());
                                    partitionInitialeDto.setSources(sources);

                                    //Pour enlever les doublons dans les sources
                                    partitionInitialeDto.setSources(partitionInitialeDto.getSources().stream().distinct().collect(Collectors.toList()));

                                    return partitionInitialeDto;
                                })
                                .last();
                     }
                )
                .doOnError(e -> log.warn( "Name not found or failed to parsing XML from SUDOC Server" + e.getMessage()))
                .onErrorResume(x -> Mono.just(new PartitionInitialeDto("","",new ArrayList<>(),new ArrayList<>(),new ArrayList<>())));
    }

    private Flux<ReferenceAutorite> referenceAutoriteDtoMono(String ppn) {

        AtomicInteger counter = new AtomicInteger(0);
        WebClient webClient = webClientBuilder.baseUrl("https://www.sudoc.fr/").build();

        return webClient.get().uri( uriBuilder -> uriBuilder
                        .path(ppn + ".abes")
                        .build() )
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .retrieve()
            .bodyToMono(XmlRootRecord.class)
            .doOnError(v -> log.error("ERROR => {}", v.getMessage()))
            .onErrorResume(v -> Mono.empty())
            .flatMapIterable(XmlRootRecord::getDatafieldList)
            .parallel().runOn(Schedulers.boundedElastic())
            .filter(v -> v.getTag().startsWith("70"))
            .doOnEach(v -> counter.getAndIncrement())
            .filter(v -> v.getSubfieldList()
                    .stream()
                    .noneMatch(t -> t.getCode().equals("3"))
            )
            .map(v -> {
                /*log.info("Execute by Thread : " + Thread.currentThread().getName());
                System.out.println("TAG = " + v.getTag());
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
            .sequential()
            .onErrorResume(v -> Flux.just(new ReferenceAutorite()));
    }

    //Utility function ( Check doublon with key of Java Stream() )
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;

    }
}
