package fr.abes.oraclewebservice.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class LibRole {

    @Id
    private String code;
    private String fr;
    private String en;
}
