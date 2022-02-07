package fr.abes.findrc.domain.repository;

import fr.abes.findrc.domain.entity.ReferenceAutoriteFromOracle;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.annotations.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReferenceAutoriteOracle {

    private final Database db;

    public Flux<ReferenceAutoriteFromOracle> getEntityWithPos(String ppn) {
        Flowable<ReferenceAutoriteFromOracle> referenceAutoriteFromOracleFlowable = db.select(
                "select * from biblio_table_frbr_extend a where\n" +
                "(a.tag='700$a' or a.tag='701$a' or a.tag='702$a'\n" +
                "or a.tag='700$b' or a.tag='701$b' or a.tag='702$b'\n" +
                "or a.tag='700$3' or a.tag='701$3' or a.tag='702$3')  and\n" +
                "a.ppn=?\n" +
                "order by posfield, possubfield")
                .parameter(ppn)
                .autoMap(ReferenceAutoriteFromOracle.class);


        return Flux.from(referenceAutoriteFromOracleFlowable);
    };
}
