package fr.abes.attrrc.domain.repository;

import fr.abes.attrrc.domain.entity.DomainCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DomainCodeOracle extends ReactiveCrudRepository<DomainCode, String> {

    @Query("select code,valeure from BIBLIO_TABLE_LIEN_RAMEAU where ppn= :ppn and valeure is not null")
    Flux<DomainCode> getCode(String ppn);
}
