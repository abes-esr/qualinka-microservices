package fr.abes.oraclewebservice.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Citation {

    @Id
    private String ppn;
    private String citation1;
    private String citation3;

}
