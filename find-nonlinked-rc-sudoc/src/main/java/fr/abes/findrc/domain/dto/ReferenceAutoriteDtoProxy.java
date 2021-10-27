package fr.abes.findrc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceAutoriteDtoProxy {


    private int count;
    private List<ReferenceAutoriteDto> ids;
}
