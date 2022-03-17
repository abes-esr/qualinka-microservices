package fr.abes.attrra.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.abes.attrra.domain.entity.Translitteration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "result")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RADto {
    String id;
    String dateCreationNotice;
    List<String> noteGen;
    List<Translitteration> preferedform;
    List<String> variantform;
    String birth;
    String death;
    String gender;
    String country;
    List<String> bioNote;
    List<String> source;
}
