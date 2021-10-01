package fr.abes.partitionInitiale.domain.utils;

import fr.abes.partitionInitiale.domain.dto.ReferenceAutoriteDto;
import fr.abes.partitionInitiale.domain.dto.ReferenceAutoriteDtoProxy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface MapStructMapper {

    @Mappings({
            @Mapping(target="referenceContextuel", source="referenceAutoriteDtoProxy.referenceAutorite")
    })
    ReferenceAutoriteDto ReferenceProxyToDto (ReferenceAutoriteDtoProxy referenceAutoriteDtoProxy);

}
