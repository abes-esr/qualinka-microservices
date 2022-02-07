package fr.abes.attrrc.domain.dto;

import lombok.Data;
import org.davidmoten.rx.jdbc.annotations.Column;

public interface CitationDto {

    @Column
    String citation1();
    @Column
    String citation3();

}
