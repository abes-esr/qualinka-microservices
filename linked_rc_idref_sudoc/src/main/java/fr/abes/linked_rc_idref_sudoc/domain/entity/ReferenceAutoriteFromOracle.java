package fr.abes.linked_rc_idref_sudoc.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class ReferenceAutoriteFromOracle {

    @Id
    private String ppn;
    private String posfield;
    private String tag;
    private String datas;
}
