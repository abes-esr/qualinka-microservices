package fr.abes.attrra.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import fr.abes.attrra.domain.dto.RADto;

import fr.abes.attrra.domain.entity.ReferenceAutorite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttrRAService {

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

    public Mono<RADto> attributs(String ppn) {

        RADto ra = new RADto();
        ArrayList<String> notesGen = new ArrayList<>();
        ArrayList<String> variantForms = new ArrayList<>();

        WebClient webClient = webClientBuilder.baseUrl(this.solrBaseUrl)
                 //.filter(logRequestWebclient()) // <== Use this for see WebClient request
                .build();

        ObjectMapper mapper = new ObjectMapper();

        return webClient.get().uri(builder -> builder
                    .path("/solr/sudoc/select")
                    .queryParam("q", "ppn_z:"+ppn)
                    .queryParam("start", "0")
                    .queryParam("rows", "1")
                    .queryParam("fl", "ppn_z,A100-a-pos0-7_AS,A300.A300Sa_AS,A900.A900Sa_AS,A901.A901Sa_AS,A103-a-pos1-4_AS,A103-b-pos1-4_AS,A700-7-pos4-5_AS,A102.A102Sa_AS,A810.A810Sa_AS")
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
                        ArrayList<ReferenceAutorite> liste = reader.<ArrayList<ReferenceAutorite>>readValue(v);
                        if (liste.size()>0) {
                            //On peut pas mapper automatiquement ??
                            ReferenceAutorite ref = liste.get(0);
                            ra.setId(ref.getId());
                            ra.setDateCreationNotice(ref.getDateCreationNotice());
                            ra.setNoteGen(ref.getNoteGen());
                            ra.setPreferedform(ref.getPreferedform());
                            ra.setVariantform(ref.getVariantform());
                            ra.setBirth(ref.getBirth());
                            ra.setDeath(ref.getDeath());
                            ra.setGender(ref.getGender());
                            ra.setCountry(ref.getCountry());
                            ra.setSource(ref.getSource());
                        }
                        return ra;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ra;
                    }
                });
    }

}
