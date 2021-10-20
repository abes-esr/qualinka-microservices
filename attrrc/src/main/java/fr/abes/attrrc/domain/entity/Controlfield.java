package fr.abes.attrrc.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


@NoArgsConstructor
@Getter
public class Controlfield {
    @XmlAttribute(name="tag")
    private String tag;
    @XmlValue
    private String controlfield;

}
