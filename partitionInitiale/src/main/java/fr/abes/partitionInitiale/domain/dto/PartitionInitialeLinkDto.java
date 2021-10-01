package fr.abes.partitionInitiale.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartitionInitialeLinkDto {
    String source;
    String type;
    String target;
}
