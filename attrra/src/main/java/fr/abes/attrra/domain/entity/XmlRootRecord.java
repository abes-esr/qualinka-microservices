package fr.abes.attrra.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@NoArgsConstructor
@Getter
@XmlRootElement(name="record")
public class XmlRootRecord {
    @XmlElement(name = "controlfield")
    private List<Controlfield> controlfieldList;

    @XmlElement(name = "datafield")
    private List<Datafield> datafieldList;
}
