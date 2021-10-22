package fr.abes.attrrc.domain.repository;

import fr.abes.attrrc.domain.entity.Citation;
import fr.abes.attrrc.domain.entity.DomainCode;
import fr.abes.attrrc.domain.entity.LibRole;
import fr.abes.attrrc.domain.entity.XmlRootRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="oracle-webservice")
public interface OracleProxy {

    @GetMapping("xmlrootrecord/{ppn}")
    Mono<XmlRootRecord> getXmlRootRecord(@PathVariable String ppn) ;

    @GetMapping("citation/{ppn}")
    Mono<Citation> getCitation(@PathVariable String ppn) ;

    @GetMapping("libcode/{code}")
    Mono<LibRole> getLibCode(@PathVariable String code) ;

    @GetMapping("domaincode/{ppn}")
    Flux<DomainCode> getDomainCode(@PathVariable String ppn) ;
}
