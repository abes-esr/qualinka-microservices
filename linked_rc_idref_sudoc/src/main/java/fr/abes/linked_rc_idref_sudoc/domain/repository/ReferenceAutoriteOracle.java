package fr.abes.linked_rc_idref_sudoc.domain.repository;

import fr.abes.linked_rc_idref_sudoc.domain.entity.ReferenceAutoriteFromOracle;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReferenceAutoriteOracle extends ReactiveCrudRepository<ReferenceAutoriteFromOracle, String> {

    @Query("select * from biblio_table_frbr_extend a where\n" +
            "(a.tag='700$a' or a.tag='701$a' or a.tag='702$a'\n" +
            "or a.tag='700$b' or a.tag='701$b' or a.tag='702$b'\n" +
            "or a.tag='700$3' or a.tag='701$3' or a.tag='702$3')  and\n" +
            "a.ppn=:ppn\n" +
            "order by posfield, possubfield")
    Flux<ReferenceAutoriteFromOracle> getEntityWithPos(String ppn);
}
