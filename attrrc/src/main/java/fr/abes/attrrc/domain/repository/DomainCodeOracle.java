package fr.abes.attrrc.domain.repository;

import fr.abes.attrrc.domain.entity.DomainCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface DomainCodeOracle extends ReactiveCrudRepository<DomainCode, String> {

    @Query("select code,valeure from BIBLIO_TABLE_LIEN_RAMEAU where \n" +
            "ppn=:ppn \n" +
            "and valeure is not null")
    Flux<DomainCode> getCode(String ppn);
}
