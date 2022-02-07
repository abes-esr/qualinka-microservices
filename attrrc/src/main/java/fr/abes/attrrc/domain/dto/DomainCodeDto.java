package fr.abes.attrrc.domain.dto;

import lombok.Data;
import org.davidmoten.rx.jdbc.annotations.Column;


public interface DomainCodeDto {

    @Column
    String code();
    @Column
    String valeure();
}
