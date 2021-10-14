package fr.abes.attrrc.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RCDto {
    String id;
    String dateCreationNotice;
    String citation;
    String ppnAut;
    String appellation;
    List<String> rameau;
    List<String> subject600new;
    List<String> subject600;
    List<String> subject601;
    List<String> subject602;
    List<String> subject604;
    List<String> subject605;
    List<String> subject607;
    List<String> subject608;
    List<String> dewey;

    String title;
    List<String> corporateBody;
    String genre;
    List<String> cocontributor;
    String publicationDate;
    String originalPublicationDate;
    List<String> domain_code;
    List<String> domain_lib;

    List<String> otherIdDoc;
    List<String> docLang;
    List<String> originalDocLang;
    List<String> publisher;
    List<String> publisherPlace;

    String role_code;
    String role_en;
    String role_fr;
}
