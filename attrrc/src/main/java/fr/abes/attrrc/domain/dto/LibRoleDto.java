package fr.abes.attrrc.domain.dto;

import org.davidmoten.rx.jdbc.annotations.Column;

public interface LibRoleDto {

    @Column
    String code();
    @Column
    String fr();
    @Column
    String en();
}
