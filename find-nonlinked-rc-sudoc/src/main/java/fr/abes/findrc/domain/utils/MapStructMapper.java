package fr.abes.findrc.domain.utils;

import fr.abes.findrc.domain.dto.ReferenceAutoriteDto;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoProxy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface MapStructMapper {

    @Mappings({
            @Mapping(target="ids", source="referenceAutoriteDtoProxy.ids")
    })
    ReferenceAutoriteDto ReferenceProxyToDto (ReferenceAutoriteDtoProxy referenceAutoriteDtoProxy);

}
