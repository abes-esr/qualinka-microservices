package fr.abes.linked_rc_idref_sudoc.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceContextuelle {


    @JsonProperty("ppn_z")
    private String id;

}
