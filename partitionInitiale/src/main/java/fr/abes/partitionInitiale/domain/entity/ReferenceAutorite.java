package fr.abes.partitionInitiale.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceAutorite {

    private String ppn;
    private String firstName;
    private String lastName;

}
