package fr.abes.findra.domain.dto;

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
public class ReferenceAutoriteGetDto {

    private final String service = "find_ra_idref";
    private final String ra_database = "idref";
    private int count;
    private Query query;
    private final String referenceType = "ra";
    private final String entityType = "person";
    private String queries;
    private List<ReferenceAutoriteDto> ids;


    public void addQuery(String firstName, String lastName) {
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
