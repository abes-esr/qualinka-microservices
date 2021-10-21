package fr.abes.attrrc.domain.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name="record")
public class XmlRootRecord {

    @XmlElement(name = "datafield")
    private List<Datafield> datafieldList;
    @XmlElement(name = "controlfield")
    private List<Controlfield> controlfieldList;
}
