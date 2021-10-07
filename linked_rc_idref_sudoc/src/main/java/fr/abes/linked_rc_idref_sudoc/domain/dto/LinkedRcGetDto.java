package fr.abes.linked_rc_idref_sudoc.domain.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkedRcGetDto {
    private String service = "linked-rc-idref-sudoc";
    private String rc_database = "sudoc";
    private String ra_database = "idref";
    private String type = "rc";
    private String query;
    private String firstName;
    private String lastName;
    private int count;
    private List<LinkedRcDto> ids;
}
