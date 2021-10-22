package fr.abes.oraclewebservice.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class CodeLang {

    @Id
    private String posfield;
    private String datas;
}
