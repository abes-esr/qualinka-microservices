package fr.abes.oraclewebservice.controller;

import fr.abes.oraclewebservice.domain.entity.Citation;
import fr.abes.oraclewebservice.domain.entity.DomainCode;
import fr.abes.oraclewebservice.domain.entity.LibRole;
import fr.abes.oraclewebservice.domain.entity.XmlRootRecord;
import fr.abes.oraclewebservice.domain.service.OracleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
public class OracleController {

    private final OracleService oracleService;


    @GetMapping("xmlrootrecord/{ppn}")
    public Mono<XmlRootRecord> getXmlRootRecord(@PathVariable String ppn) {
        return oracleService.getXmlRootRecordOracle(ppn);
    }

    @GetMapping("citation/{ppn}")
    public Mono<Citation> getCitation(@PathVariable String ppn) {
        return oracleService.getCitationOracle(ppn);
    }

    @GetMapping("libcode/{code}")
    public Mono<LibRole> getLibCode(@PathVariable String code) {
        return oracleService.getLibRoleOracle(code);
    }

    @GetMapping("domaincode/{ppn}")
    public Flux<DomainCode> getDomainCode(@PathVariable String ppn) {
        return oracleService.getDomainOracle(ppn);
    }

}
