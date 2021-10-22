package fr.abes.oraclewebservice.domain.repository;

import fr.abes.oraclewebservice.domain.entity.Citation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface CitationOracle extends ReactiveCrudRepository<Citation, String> {

    @Query("select ppn, citation1, citation3 from BIBLIO_TABLE_GENERALE c where c.ppn= :ppn and ROWNUM = 1")
    Mono<Citation> getCitation(String ppn);


}