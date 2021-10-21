package fr.abes.attrrc.domain.repository;

import fr.abes.attrrc.domain.entity.CodeLang;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CodeLangOracle extends ReactiveCrudRepository<CodeLang, String> {

    @Query("select posfield,datas,tag from BIBLIO_TABLE_FRBR_EXTEND where ppn= :ppn " +
            "and tag like '70%'  and tag like '%$4' order by to_number(posfield),possubfield asc")
    Flux<CodeLang> getCode(String ppn);




}
