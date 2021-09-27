package fr.abes.findra.domain.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceAutoriteGetDto {

    private int ppnCounter;
    private List<ReferenceAutoriteDto> referenceAutorite;
}
