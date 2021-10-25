package fr.abes.linked_rc_idref_sudoc.domain.entity;

import org.davidmoten.rx.jdbc.annotations.Column;

public interface ReferenceAutoriteFromOracle {
    @Column
    String ppn();
    @Column
    String posfield();
    @Column
    String tag();
    @Column
    String datas();
}
