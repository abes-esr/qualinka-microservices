package fr.abes.linked_rc_idref_sudoc.controller;

import com.google.common.base.Strings;
import fr.abes.linked_rc_idref_sudoc.domain.dto.LinkedRcGetDto;
import fr.abes.linked_rc_idref_sudoc.domain.service.LinkedRcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/")
public class LinkedRcController {

    private final LinkedRcService linkedRcService;
    private final ResourceLoader resourceLoader;


    @GetMapping("req")
    @Operation(description = "A partir d'un identifiant IdRef de notice d’autorité (sous la forme ex. 123456879), le service renvoie les points d’accès des notices bibliographiques Sudoc liés à une autorité de la base IdRef (sous la forme ppn + \"-\" + sa position à partir des zones 70X ; ex. 123456789-1).  <br/><br/>" +
            "Avec le paramètre \"file\", il est possible d’appeler un fichier de requêtes en particulier, afin d’ajuster les requêtes passées à l’appellation en entrée (ex : réduite à un lastName) ou le degré de finesse des résultats (ex : recherche étroite).  <br/><br/>" +
            "Ce service est le complémentaire du service \"find-nonlinked-rc-sudoc\".",
                parameters = {
                    @Parameter(name = "ra_id", in = ParameterIn.QUERY, required = true, example = "076642860", description = "Identifiant IdRef de notice d’autorité"),
                    @Parameter(name = "format", in = ParameterIn.QUERY, required = false, description = "Format de la réponse : xml, json (défaut)")
                }
    )
    public Mono<LinkedRcGetDto> getLinkedRc(
                                                @RequestParam(value="ra_id") String ra_id
                                                ) {

        return linkedRcService.findLinkedRc(ra_id);


    }

}
