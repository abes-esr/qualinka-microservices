package fr.abes.attrrc.domain.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import fr.abes.attrrc.domain.dto.CitationDto;
import fr.abes.attrrc.domain.dto.DomainCodeDto;
import fr.abes.attrrc.domain.dto.LibRoleDto;
import fr.abes.attrrc.domain.entity.XmlRootRecord;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.davidmoten.rx.jdbc.Database;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class OracleReferenceAuth {

    private final Database db;

    public Mono<XmlRootRecord> getXmlRootRecordOracle(String ppn){

        Flowable<XmlRootRecord> xmlRootRecordFlowable = db
                .select("select data_xml from NOTICESBIBIO where ppn = ?").queryTimeoutSec(30)
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

    public Mono<CitationDto> getCitationOracle(String ppn) {

        Flowable<CitationDto> citationDtoFlowable = db.select("select citation1, citation3 from BIBLIO_TABLE_GENERALE where ppn=?").queryTimeoutSec(30)
                .parameter(ppn)
                .autoMap(CitationDto.class);
        return Mono.from(citationDtoFlowable);
    }

    public Flux<DomainCodeDto> getDomainCodeAndValue(String ppn) {

        Flowable<DomainCodeDto> domainCodeDtoFlowable = db.select("select code, valeure from BIBLIO_TABLE_LIEN_RAMEAU where ppn=? and valeure is not null").queryTimeoutSec(30)
                .parameter(ppn)
                .autoMap(DomainCodeDto.class);
        return Flux.from(domainCodeDtoFlowable);

    }

    public Mono<LibRoleDto> getLibRoleOracle(String code) {

        Flowable<LibRoleDto> libRoleDtoFlowable = db.select("select code, RELATIONSHIP_FR as fr, RELATIONSHIP_EN as en from FNCT_MARC21 where code=?").queryTimeoutSec(30)
                .parameter(code)
                .autoMap(LibRoleDto.class);

        return Mono.from(libRoleDtoFlowable);

    }

    public Flux<String> getkeywordOracle(String ppn) {
        Flowable<String> keywords = db.select("select datas from BIBLIO_TABLE_FRBR_EXTEND where ppn= ? and tag = '610$a'").queryTimeoutSec(30)
                .parameter(ppn)
                .get(v -> v.getString(1));
        return Flux.from(keywords);
    }




}
