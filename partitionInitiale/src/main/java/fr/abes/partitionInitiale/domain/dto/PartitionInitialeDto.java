package fr.abes.partitionInitiale.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PartitionInitialeDto {
    //https://paprika.idref.fr/ws/qualinca/partitionattr/partatt?prenom=yann&nom=nicolas
    //initialLinks:[{source:"sudoc:229023274-1",type:"sameAs",target:"idref:229012132"},...]
    //sources:["sudoc:071625739-1",...]
    //"scenario":"","supports":"",
    //"targets":["idref:229012132",...]
    String scenario;
    String supports;
    List<PartitionInitialeLink> initialLinks;
    List<String> sources;
    List<String> targets;

}
