package fr.abes.attrra.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Translitteration {

    private String script = "ba";
    private String value;

}
