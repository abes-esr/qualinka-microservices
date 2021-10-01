package fr.abes.attrrc.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RCDto {
    String id;
    String dateCreationNotice;
    List<String> noteGen;
    String preferedform;
    List<String> variantform;
    //List<String> parallelform;//902$a pas fait dans l'ancienne version ?
    String birth;
    String death;
    String gender;
    String country;
    List<String> bioNote;
    List<String> source;
}
