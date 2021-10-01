package fr.abes.partitionInitiale.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartitionInitialeDto {
    String scenario;
    String supports;
    List<PartitionInitialeLinkDto> initialLinks;
    List<String> sources;
    List<String> targets;

}
