package fr.abes.oraclewebservice.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class DomainCode {

    @Id
    private String code;
    private String valeure;
}
