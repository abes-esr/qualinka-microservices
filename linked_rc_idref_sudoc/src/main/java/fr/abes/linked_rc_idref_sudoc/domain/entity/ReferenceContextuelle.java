package fr.abes.linked_rc_idref_sudoc.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceContextuelle {
    @JsonProperty("ppn_z")
    private String ppn;
}
