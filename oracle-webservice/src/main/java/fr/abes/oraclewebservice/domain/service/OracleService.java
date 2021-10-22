package fr.abes.oraclewebservice.domain.service;

import fr.abes.oraclewebservice.domain.entity.Citation;
import fr.abes.oraclewebservice.domain.entity.DomainCode;
import fr.abes.oraclewebservice.domain.entity.LibRole;
import fr.abes.oraclewebservice.domain.entity.XmlRootRecord;
import fr.abes.oraclewebservice.domain.repository.CitationOracle;
import fr.abes.oraclewebservice.domain.repository.DomainCodeOracle;
import fr.abes.oraclewebservice.domain.repository.LibRoleOracle;
import fr.abes.oraclewebservice.domain.repository.ReferenceAutoriteOracle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Slf4j
@Service
public class OracleService {

    private final ReferenceAutoriteOracle referenceAutoriteOracle;
    private final LibRoleOracle libRoleOracle;
    private final DomainCodeOracle domainCodeOracle;
    private final CitationOracle citationOracle;

   public Mono<LibRole> getLibRoleOracle(String code) {
       return libRoleOracle.getLib(code);
   }

   public Mono<Citation> getCitationOracle(String ppn) {
       return citationOracle.getCitation(ppn);
   }

   public Mono<XmlRootRecord> getXmlRootRecordOracle(String ppn) {
       return referenceAutoriteOracle.getEntityWithPpn(ppn);
   }

   public Flux<DomainCode> getDomainOracle(String ppn) {
       return domainCodeOracle.getCode(ppn);
   }


}
