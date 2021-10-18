package fr.abes.attrrc.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

@Getter
@NoArgsConstructor
public class Subfield {

    @XmlValue
    private String subfield;

    @XmlAttribute(name="code")
    private String code;

}
