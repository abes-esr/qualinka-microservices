package fr.abes.findrc.domain.entity;

import lombok.Data;
import oracle.sql.CHAR;
import org.springframework.data.annotation.Id;

@Data
public class ReferenceAutoriteFromOracle {

    @Id
    private String ppn;
    private String posfield;
    private String tag;
    private String datas;
}
