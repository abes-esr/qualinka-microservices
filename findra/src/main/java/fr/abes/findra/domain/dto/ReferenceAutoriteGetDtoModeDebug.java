package fr.abes.findra.domain.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "SolrRequest", "found", "results" })
public class ReferenceAutoriteGetDtoModeDebug {


    private String SolrRequest;
    private int found;
    private List<ReferenceAutoriteDto> results;
}