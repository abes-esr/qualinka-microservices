package fr.abes.linked_rc_idref_sudoc.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceAutorite {

    private int id;
    @JsonProperty("ppn_z")
    private String ppn;

    private String firstName;
    private String lastName;



    @JsonSetter("A200.A200Sb_AS")
    public void setValueInternalFirstNameRA(JsonNode valueInternal) {

        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.firstName = valueInternal.get(0).asText();
            }
        }

    }

    @JsonSetter("A200.A200Sa_AS")
    public void setValueInternalLastNameRA(JsonNode valueInternal) {

        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.lastName = valueInternal.get(0).asText();
            }
        }

    }

}
