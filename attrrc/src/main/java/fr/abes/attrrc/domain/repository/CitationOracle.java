package fr.abes.attrrc.domain.repository;

import fr.abes.attrrc.domain.entity.Citation;
import fr.abes.attrrc.domain.entity.CodeLang;
import fr.abes.attrrc.domain.entity.LibRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CitationOracle extends ReactiveCrudRepository<Citation, String> {

    @Query("select ppn, citation1, citation3 from BIBLIO_TABLE_GENERALE c where c.ppn= :ppn and ROWNUM = 1")
    Mono<Citation> getCitation(String ppn);


}