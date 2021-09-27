package fr.abes.findra.domain.utils;

import fr.abes.findra.domain.dto.ReferenceAutoriteDto;
import fr.abes.findra.domain.entity.ReferenceAutorite;
import org.mapstruct.Mapper;

@Mapper
public interface MapStructMapper {

    ReferenceAutoriteDto referenceAutoriteToreferenceAutoriteDto (ReferenceAutorite referenceAutorite);

}
