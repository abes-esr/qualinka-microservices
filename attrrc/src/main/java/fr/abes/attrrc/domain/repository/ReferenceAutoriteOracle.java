package fr.abes.attrrc.domain.repository;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import fr.abes.attrrc.domain.entity.CodeLang;
import fr.abes.attrrc.domain.entity.XmlRootRecord;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.xdb.XMLType;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ReferenceAutoriteOracle {

    private final ConnectionFactory connectionFactory ;
    private final WebClient.Builder webClientBuilder;

    public Mono<XmlRootRecord> getEntityWithPpnWebclient(String ppn) {

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
                .onErrorResume(v -> Mono.empty());


    }

    public Mono<XmlRootRecord> getEntityWithPpn(String ppn) {

        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                                                .createStatement("select DATA_XML from NOTICESBIBIO where ppn = :ppn")
                                                .bind("ppn", ppn)
                                                .execute())
                    .flatMap(result ->result.map((row, rowMetadata) -> row.get(0, XMLType.class)))
                    .doFinally(t -> subscribeClose(connection))
                    .map(v -> {
                        try {
                            /*String xmlString = v.getString();
                            System.out.println(xmlString);
                            JAXBContext jaxbContext = JAXBContext.newInstance(XmlRootRecord.class);
                            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                            return (XmlRootRecord) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));*/


                            String xmlString = v.getString();

                            JacksonXmlModule xmlModule = new JacksonXmlModule();
                            xmlModule.setDefaultUseWrapper(false);
                            ObjectMapper objectMapper = new XmlMapper(xmlModule);
                            objectMapper.registerModule(new JaxbAnnotationModule());

                            return objectMapper.readValue(new StringReader(xmlString), XmlRootRecord.class);

                        }  catch (SQLException | IOException e) {
                            e.printStackTrace();
                            return new XmlRootRecord();
                        }
                    }))
                    .last()
                    .doOnError(e -> log.warn("No result from SQL with ppn {}", ppn))
                    .onErrorResume(e -> Mono.empty());

    }

    public Flux<Map<String,String>> getDomainAndCodeWithPpn(String ppn) {

        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                        .createStatement("select code,valeure from BIBLIO_TABLE_LIEN_RAMEAU where ppn= :ppn")
                        .bind("ppn",ppn)
                        .execute())
                .flatMap(result -> result.map( (row, rowMetadata) -> {
                    Map<String,String> map = new HashMap<>();
                    map.put(row.get(0, String.class), row.get(1, String.class));
                    return map;
                }))
                .doFinally(t -> subscribeClose(connection)));

    }

    public Mono<String> getCode(String ppn, int pos) {
        List<String> codeList = new ArrayList<>();
         return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                        .createStatement("select posfield,datas,tag from BIBLIO_TABLE_FRBR_EXTEND where ppn= :ppn and tag like '70%'  and tag like '%$4' order by to_number(posfield),possubfield asc")
                        .bind("ppn", ppn)
                        .execute())
                    .flatMap(result -> result.map( ((row, rowMetadata) -> row.get(1, String.class))))
                    .doFinally(t -> subscribeClose(connection))
                )
                 .index()
                 .filter(t -> t.getT1() == pos-1)
                 .flatMap(t -> Mono.just(t.getT2()))
                 .last()
                 .doOnError(e -> log.warn("No result from SQL for the code lang with the PPN {}", ppn));
    }

    /*public Mono<CodeLang> getCodeLangFrEn(String code) {

        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                                .createStatement("select code, RELATIONSHIP_FR as fr, RELATIONSHIP_EN as en from FNCT_MARC21 where code= :code")
                                .bind("code",code)
                                .execute())
                        .flatMap(result -> result.map( (row, rowMetadata) ->
                                CodeLang.builder().fr(row.get(1, String.class)).en(row.get(2, String.class)).build())
                        )
                        .doFinally(t -> subscribeClose(connection))
                )
                .last()
                .doOnError(e -> log.warn("No result from SQL with the code lang {}", code));
    }*/

    /*public Mono<String> getcitation(String ppn) {

        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                                .createStatement("select citation1,citation3 from BIBLIO_TABLE_GENERALE where ppn= :ppn")
                                .bind("ppn",ppn)
                                .execute())
                        .flatMap(result -> result.map( (row, rowMetadata) ->
                                row.get(0, String.class) + "/" + row.get(1, String.class))
                        )
                        .doFinally(t -> subscribeClose(connection))
                )
                .last()
                .doOnError(e -> log.warn("No result from SQL with the ppn {}", ppn));
    }*/

    private static void subscribeClose(Connection connection) {
        Mono.from(connection.close()).subscribe();
    }
}
