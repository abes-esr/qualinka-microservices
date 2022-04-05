package fr.abes.attrrc.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.davidmoten.rx.jdbc.annotations.Column;


public interface DomainCodeDto {

    @Column
    @JsonProperty("code")
    String code();
    @Column
    @JsonProperty("lib")
    String valeure();


}
