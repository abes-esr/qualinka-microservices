package fr.abes.oraclewebservice.domain.repository;

import fr.abes.oraclewebservice.domain.entity.DomainCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface DomainCodeOracle extends ReactiveCrudRepository<DomainCode, String> {

    @Query("select code,valeure from BIBLIO_TABLE_LIEN_RAMEAU where ppn= :ppn and valeure is not null")
    Flux<DomainCode> getCode(String ppn);
}
