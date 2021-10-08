package fr.abes.attrra.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.attrra.domain.dto.RADto;

import fr.abes.attrra.domain.entity.XmlRootRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
        WebClient webClient = webClientBuilder.baseUrl("https://www.idref.fr/").build();
        ObjectMapper mapper = new ObjectMapper();

        List<String> noteGen = new ArrayList<>();
        List<String> variantform = new ArrayList<>();
        List<String> bioNote = new ArrayList<>();
        List<String> source = new ArrayList<>();
        ra.setId(ppn);

        return webClient.get().uri( uriBuilder -> uriBuilder
                .path(ppn + ".xml")
                .build() )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToMono(XmlRootRecord.class)
                .doOnError(v -> log.error("ERROR => {}", v.getMessage()))
                .onErrorResume(v -> Mono.empty())
                //.flatMapIterable(XmlRootRecord::getDatafieldList)
                .map(v -> {

                    v.getControlfieldList().stream().forEach(c -> {
                        if (c.getTag().equals("004")){
                            ra.setDateCreationNotice(c.getValue());
                        }
                    });

                    v.getDatafieldList().stream().forEach(d -> {
                        d.getSubfieldList().stream().forEach(s -> {
                            //System.out.println(d.getTag() + " : " + s.getCode() + " : " + s.getSubfield());
                            if (d.getTag().equals("300") && s.getCode().equals("a")){
                                noteGen.add(s.getSubfield());
                                ra.setNoteGen(noteGen);
                            }

                            if (d.getTag().equals("901") && s.getCode().equals("a")) {
                                variantform.add(s.getSubfield());
                                ra.setVariantform(variantform);
                            }

                            if (d.getTag().equals("900") && s.getCode().equals("a")) {
                                ra.setPreferedform(s.getSubfield());
                            }

                            if (d.getTag().equals("103") && s.getCode().equals("a")) {
                                String dateBirth = s.getSubfield().trim();
                                if (dateBirth.length()>4) {
                                    dateBirth = dateBirth.substring(0,4);
                                }
                                ra.setBirth(dateBirth);
                            }

                            if (d.getTag().equals("103") && s.getCode().equals("b")) {
                                String dateDeath = s.getSubfield().trim();
                                if (dateDeath.length()>4) {
                                    dateDeath = dateDeath.substring(0,4);
                                }
                                ra.setDeath(dateDeath);
                            }

                            if (d.getTag().equals("120") && s.getCode().equals("a")) {
                                ra.setGender(s.getSubfield());
                            }

                            if (d.getTag().equals("102") && s.getCode().equals("a")) {
                                ra.setCountry(s.getSubfield());
                            }

                            if (d.getTag().equals("340") && s.getCode().equals("a")) {
                                bioNote.add(s.getSubfield());
                                ra.setBioNote(bioNote);
                            }

                            if (d.getTag().equals("810") && s.getCode().equals("a")) {
                                source.add(s.getSubfield());
                                ra.setSource(source);
                            }
                        });
                    });

                    return ra;
                })
                .onErrorResume(v -> Mono.just(new RADto()));
    }

}
