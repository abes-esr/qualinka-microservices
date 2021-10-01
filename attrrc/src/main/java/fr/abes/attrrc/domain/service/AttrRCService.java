package fr.abes.attrrc.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.abes.attrrc.domain.dto.RCDto;

import fr.abes.attrrc.domain.entity.ReferenceContextuelle;
import fr.abes.attrrc.domain.entity.XmlRootRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttrRCService {

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

    public Mono<RCDto> attributs(String ppn) {

        RCDto rc = new RCDto();
        WebClient webClient = webClientBuilder.baseUrl("https://www.sudoc.fr/").build();
        //AtomicInteger counter = new AtomicInteger(0);

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
                /*.parallel().runOn(Schedulers.boundedElastic())
                .filter(v -> v.getTag().startsWith("70"))
                .doOnEach(v -> counter.getAndIncrement())
                .filter(v -> v.getSubfieldList()
                        .stream()
                        .noneMatch(t -> t.getCode().equals("3"))
                )*/
                .map(v -> {
                /*log.info("Execute by Thread : " + Thread.currentThread().getName());*/
                System.out.println("TAG = " + v.getTag());
                //System.out.println("PPN = " + ppn);
                //System.out.println("POS = " + counter.get());

                    v.getSubfieldList().forEach(t -> {
                                System.out.println(t.getCode() + " : " + t.getSubfield());
                                rc.setId(ppn );
                                if (t.getCode().equals("a")) {
                                    //referenceAutoriteDto.setLastName(t.getSubfield());
                                    rc.setBirth(t.getSubfield());
                                }
                                if (t.getCode().equals("b")) {
                                    //referenceAutoriteDto.setFirstName(t.getSubfield());
                                    rc.setDeath(t.getSubfield());
                                }
                            }
                    );
                    /*System.out.println("==================================");*/
                    return rc;
                })
                //.sequential()
                .last()
                .onErrorResume(v -> Mono.just(new RCDto()));
    }

}
