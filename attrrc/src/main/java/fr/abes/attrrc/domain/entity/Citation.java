package fr.abes.attrrc.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Citation {

    @Id
    private String ppn;
    private String citation1;
    private String citation3;

}
