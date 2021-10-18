package fr.abes.attrrc.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@NoArgsConstructor
@Getter
public class Datafield {

    @XmlAttribute(name="tag")
    private String tag;

    @XmlElement(name="subfield")
    private List<Subfield> subfieldList;
}
