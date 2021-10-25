package fr.abes.findrc.domain.dto;

import fr.abes.findrc.domain.entity.ReferenceAutorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "result")
public class ReferenceAutoriteDto {

    private final String service ="find_nonlinked_rc";
    private int count;
    private final String rc_database = "sudoc";
    private Query query;
    private final String referenceType = "rc";
    private final String entityType = "Person";
    private String queries;
    private List<ReferenceAutorite> ids;

    public void addQuery (String firstName, String lastName) {
        this.query = new Query(firstName, lastName);
    }

    private static class Query {
        @Getter
        private String firstName;
        @Getter
        private String lastName;

        public Query(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}
