package fr.abes.attrrc.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RCDto {
    private String id;
    private String dateCreationNotice;
    private String citation;
    private String ppnAut;
    private String appellation;
    private List<String> rameau;
    private String subject600new;
    private String subject600;
    private List<String> subject607;
    private String subject608;
    private String dewey;
    private String title;
    private List<String> cocontributor;
    private String publicationDate;
    private List<String> domain_code;
    private List<String> domain_lib;
    private List<String> otherIdDoc;
    private String docLang;
    private String publisher;
    private String publisherPlace;
    private String role_code;
    private String role_en;
    private String role_fr;
}
