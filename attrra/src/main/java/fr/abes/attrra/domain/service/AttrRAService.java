package fr.abes.attrra.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.attrra.domain.dto.RADto;

import fr.abes.attrra.domain.entity.XmlRootRecord;
import fr.abes.attrra.domain.repository.OracleReferenceAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AttrRAService {

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

    public Mono<RADto> attributs(String ppn) {

        RADto ra = new RADto();

        return oracleReferenceAuth.getXmlRootRecordOracle(ppn)
                .publishOn(Schedulers.boundedElastic())
                .map(v -> {

                    ra.setId(ppn);

                    v.getControlfieldList()
                            .stream()
                            .filter(t -> t.getTag().equals("004"))
                            .findFirst()
                            .ifPresent(t -> ra.setDateCreationNotice(t.getControlfield()));

                    ra.setNoteGen(v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("300"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    ra.setVariantform(v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("901"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("900"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .findFirst()
                            .ifPresent(t -> ra.setPreferedform(t.getSubfield()));

                    v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("103"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .findFirst()
                            .ifPresent(t -> {
                                String dateBirth = t.getSubfield().trim();
                                if (dateBirth.length()>4) {
                                    dateBirth = dateBirth.substring(0,4);
                                }
                                ra.setBirth(dateBirth);
                            });

                    v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("103"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("b"))
                            .findFirst()
                            .ifPresent(t -> {
                                String dateDeath = t.getSubfield().trim();
                                if (dateDeath.length()>4) {
                                    dateDeath = dateDeath.substring(0,4);
                                }
                                ra.setDeath(dateDeath);
                            });

                    v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("120"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .findFirst()
                            .ifPresent(t -> ra.setGender(t.getSubfield()));

                    v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("102"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .findFirst()
                            .ifPresent(t -> ra.setCountry(t.getSubfield()));

                    ra.setBioNote(v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("340"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collector.of(ArrayList::new, ArrayList::add, (a, b) -> a, a -> null)));

                    ra.setSource(v.getDatafieldList().stream()
                            .filter(t -> t.getTag().equals("810"))
                            .flatMap(t -> t.getSubfieldList().stream())
                            .filter(t -> t.getCode().equals("a"))
                            .map(t -> String.valueOf(t.getSubfield()))
                            .collect(Collectors.toList()));

                    return ra;
                })
                .onErrorResume(v -> Mono.just(new RADto()));
    }

}
