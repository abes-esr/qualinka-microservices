package fr.abes.attrra.domain.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import fr.abes.attrra.domain.entity.XmlRootRecord;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.davidmoten.rx.jdbc.Database;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StringReader;

@Repository
@RequiredArgsConstructor
public class OracleReferenceAuth {

    private final Database db;

    public Mono<XmlRootRecord> getXmlRootRecordOracle(String ppn){

        Flowable<XmlRootRecord> xmlRootRecordFlowable = db
                .select("select data_xml from NOTICES where ppn = ?").queryTimeoutSec(30)
                .parameter(ppn)
                .get(v -> {
                    JacksonXmlModule xmlModule = new JacksonXmlModule();
                    xmlModule.setDefaultUseWrapper(false);
                    ObjectMapper objectMapper = new XmlMapper(xmlModule);
                    objectMapper.registerModule(new JaxbAnnotationModule());

                    try {
                        return objectMapper.readValue(new StringReader(v.getString(1)), XmlRootRecord.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new XmlRootRecord();
                    }
                });
        return Mono.from(xmlRootRecordFlowable);
    }

}
