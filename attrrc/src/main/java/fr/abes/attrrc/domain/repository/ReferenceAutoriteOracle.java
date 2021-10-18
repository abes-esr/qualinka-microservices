package fr.abes.attrrc.domain.repository;

import fr.abes.attrrc.domain.dto.RCDto;
import fr.abes.attrrc.domain.entity.CodeLang;
import fr.abes.attrrc.domain.entity.XmlRootRecord;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oracle.xdb.XMLType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReferenceAutoriteOracle {

    private final ConnectionFactory connectionFactory ;

    public Mono<XmlRootRecord> getEntityWithPpn(String ppn) {
        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                                                .createStatement("select DATA_XML from NOTICESBIBIO where ppn = :ppn")
                                                .bind("ppn", ppn)
                                                .execute())
                    .flatMap(result ->result.map((row, rowMetadata) -> row.get(0, XMLType.class)))
                    .map(v -> {
                        try {
                            String xmlString = v.getString();
                            JAXBContext jaxbContext = JAXBContext.newInstance(XmlRootRecord.class);
                            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                            return (XmlRootRecord) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

                        } catch (SQLException | JAXBException e) {
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
                })));

    }

    public Mono<CodeLang> getCodeLangFrEn(String code) {

        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                                .createStatement("select code, RELATIONSHIP_FR as fr, RELATIONSHIP_EN as en from FNCT_MARC21 where code= :code")
                                .bind("code",code)
                                .execute())
                        .flatMap(result -> result.map( (row, rowMetadata) ->
                                CodeLang.builder().fr(row.get(1, String.class)).en(row.get(2, String.class)).build())
                        )
                )
                .last()
                .doOnError(e -> log.warn("No result from SQL with the code lang {}", code));
    }

    public Mono<String> getcitation(String ppn) {

        return Mono.from(connectionFactory.create())
                .flatMapMany(connection -> Flux.from(connection
                                .createStatement("select citation1,citation3 from BIBLIO_TABLE_GENERALE where ppn= :ppn")
                                .bind("ppn",ppn)
                                .execute())
                        .flatMap(result -> result.map( (row, rowMetadata) ->
                                row.get(0, String.class) + "/" + row.get(1, String.class))
                        )
                )
                .last()
                .doOnError(e -> log.warn("No result from SQL with the ppn {}", ppn));
    }
}
