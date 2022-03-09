package fr.abes.attrrc.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.davidmoten.rx.jdbc.annotations.Column;

public interface LibRoleDto {

    @Column
    @JsonProperty("code")
    String code();
    @Column
    @JsonProperty("fr")
    String fr();
    @Column
    @JsonProperty("en")
    String en();
}
