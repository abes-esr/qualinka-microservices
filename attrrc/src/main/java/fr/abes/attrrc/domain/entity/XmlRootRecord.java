package fr.abes.attrrc.domain.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
