package fr.abes.attrrc.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.abes.attrrc.domain.entity.Translitteration;
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
public class RCDto {
    private String id;
    private String dateCreationNotice;
    private String citation;
    private String ppnAut;
    private List<Translitteration> appelation;
    private List<String> rameau;
    private List<String> subject600new;
    private List<String> subject600;
    private List<String> subject601;
    private List<String> subject602;
    private List<String> subject604;
    private List<String> subject605;
    private List<String> subject607;
    private List<String> subject608;
    private List<String> dewey;
    private List<Translitteration> title;
    private List<String> cocontributor;
    private List<String> corporateBody;
    private String publicationDate;
    private String originalPublicationDate;
    private List<DomainCodeDto> domain;
    private List<String> otherIdDoc;
    private List<String> docLang;
    private List<String> originalDocLang;
    private List<String> publisher;
    private List<String> publisherPlace;
    private List<String> keyword;
    private List<String> mesh;
    private String genre;
    @JsonIgnore
    private List<String> role_code;
    private List<LibRoleDto> role;
    private String thesisNote;
    private boolean location;

}
