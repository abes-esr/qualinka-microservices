package fr.abes.partitionInitiale.domain.dto;

import fr.abes.partitionInitiale.domain.entity.ReferenceAutorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceAutoriteDtoProxy {


    private int ppnCounter;
    private List<ReferenceAutorite> referenceAutorite;
}
