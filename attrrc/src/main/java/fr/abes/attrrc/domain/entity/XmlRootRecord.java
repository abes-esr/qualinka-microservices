package fr.abes.attrrc.domain.entity;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@XmlRootElement(name="record")
public class XmlRootRecord {

    @XmlElement(name = "datafield")
    private List<Datafield> datafieldList;
    @XmlElement(name = "controlfield")
    private List<Controlfield> controlfieldList;
}
