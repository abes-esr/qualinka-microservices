package fr.abes.findrc.domain.dto;

import fr.abes.findrc.domain.entity.ReferenceAutorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "result")
public class ReferenceAutoriteDto {

    private int ppnCounter;
    private List<ReferenceAutorite> referenceContextuel;
}
