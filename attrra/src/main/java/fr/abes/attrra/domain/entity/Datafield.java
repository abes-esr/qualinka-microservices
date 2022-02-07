package fr.abes.attrra.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Datafield {

    @XmlAttribute(name="tag")
    private String tag;

    @XmlElement(name="subfield")
    private List<Subfield> subfieldList;
}
