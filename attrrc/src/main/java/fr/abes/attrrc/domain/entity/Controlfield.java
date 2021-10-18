package fr.abes.attrrc.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@NoArgsConstructor
@Getter
public class Controlfield {
    @XmlAttribute(name="tag")
    private String tag;
    @XmlValue
    private String controlfield;

}
