package fr.abes.attrrc.domain.repository;

import fr.abes.attrrc.domain.entity.LibRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LibRoleOracle extends ReactiveCrudRepository<LibRole, String> {

    @Query("select code, RELATIONSHIP_FR as fr, RELATIONSHIP_EN as en from FNCT_MARC21 where code= :code")
    Mono<LibRole> getLib(String code);
}
