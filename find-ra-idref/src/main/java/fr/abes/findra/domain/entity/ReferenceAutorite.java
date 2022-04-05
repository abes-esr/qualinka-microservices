package fr.abes.findra.domain.entity;

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

    private void setValueFirstName(JsonNode valueInternal){
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.firstName = valueInternal.get(0).asText();
            }
        }
    }

    private void setValueLastName(JsonNode valueInternal){
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.lastName = valueInternal.get(0).asText();
            }
        }
    }

    @JsonSetter("A200.A200Sb_AS")
    public void setValueInternalFirstNameRA(JsonNode valueInternal) {
        this.setValueFirstName(valueInternal);
    }

    @JsonSetter("A200.A200Sa_AS")
    public void setValueInternalLastNameRA(JsonNode valueInternal) {
        this.setValueLastName(valueInternal);
    }

    @JsonSetter("B700.B700Sb_BS")
    public void setValueInternalFirstName700RC(JsonNode valueInternal) {
        this.setValueFirstName(valueInternal);
    }

    @JsonSetter("B700.B700Sa_BS")
    public void setValueInternalLastName700RC(JsonNode valueInternal) {
        this.setValueLastName(valueInternal);
    }

    @JsonSetter("B701.B701Sb_BS")
    public void setValueInternalFirstName701RC(JsonNode valueInternal) {
        this.setValueFirstName(valueInternal);
    }

    @JsonSetter("B701.B701Sa_BS")
    public void setValueInternalLastName701RC(JsonNode valueInternal) {
        this.setValueLastName(valueInternal);
    }

    @JsonSetter("B702.B702Sb_BS")
    public void setValueInternalFirstName702RC(JsonNode valueInternal) {
        this.setValueFirstName(valueInternal);
    }

    @JsonSetter("B702.B702Sa_BS")
    public void setValueInternalLastName702RC(JsonNode valueInternal) {
        this.setValueLastName(valueInternal);
    }
}
