package fr.abes.findra.domain.dto;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "result")
public class ReferenceAutoriteGetDto {

    private int ppnCounter;
    private List<ReferenceAutoriteDto> referenceAutorite;
}
