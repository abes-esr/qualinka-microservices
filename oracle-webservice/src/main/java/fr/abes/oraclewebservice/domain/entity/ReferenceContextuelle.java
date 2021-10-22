package fr.abes.oraclewebservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceContextuelle {

    @JsonProperty("ppn_z")
    String id;

    //https://www.sudoc.fr/services/generic/?servicekey=qualinca_attr_ra&ppn=055326269
    //http://solrtotal.v102.abes.fr:8081/solr/sudoc/select?q=ppn_z:055326269&fl=ppn_z,A100-a-pos0-7_AS,A300.A300Sa_AS,A900.A900Sa_AS,A901.A901Sa_AS,A103-a-pos1-4_AS,A103-b-pos1-4_AS,A700-7-pos4-5_AS,A102.A102Sa_AS,A810.A810Sa_AS

    String dateCreationNotice;

    @JsonProperty("A300.A300Sa_AS")
    List<String> noteGen;

    String preferedform;

    @JsonProperty("A901.A901Sa_AS")
    List<String> variantform;

    String birth;
    String death;
    String gender;
    String country;

    @JsonSetter("A340.A340Sa_AS")
    List<String> bioNote;

    @JsonSetter("A810.A810Sa_AS")
    List<String> source;

    @JsonSetter("A100-a-pos0-7_AS")
    public void setValueInternalDateCreationNoticeRA(JsonNode valueInternal) {
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.dateCreationNotice = valueInternal.get(0).asText();
            }
        }
    }

    @JsonSetter("A900.A900Sa_AS")
    public void setValueInternalPreferedFormRA(JsonNode valueInternal) {
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.preferedform = valueInternal.get(0).asText();
            }
        }
    }

    @JsonSetter("A103-a-pos1-4_AS")
    public void setValueInternalBirthRA(JsonNode valueInternal) {
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.birth = valueInternal.get(0).asText();
            }
        }
    }

    @JsonSetter("A103-b-pos1-4_AS")
    public void setValueInternalDeathRA(JsonNode valueInternal) {
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.death = valueInternal.get(0).asText();
            }
        }
    }

    @JsonSetter("A120.A120Sa_AS")
    public void setValueInternalGenderRA(JsonNode valueInternal) {
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.gender = valueInternal.get(0).asText();
            }
        }
    }

    @JsonSetter("A102.A102Sa_AS")
    public void setValueInternalCountryRA(JsonNode valueInternal) {
        if (valueInternal != null) {
            if (valueInternal.isArray()) {
                this.country = valueInternal.get(0).asText();
            }
        }
    }

}
