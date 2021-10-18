package fr.abes.attrra.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "result")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RADto {
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
