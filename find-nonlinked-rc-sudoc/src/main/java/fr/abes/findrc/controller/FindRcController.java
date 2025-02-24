package fr.abes.findrc.controller;

import com.google.common.base.Strings;
import fr.abes.findrc.domain.dto.ReferenceAutoriteGetDto;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoDebugProxy;
import fr.abes.findrc.domain.service.ReferenceContextuelService;
import fr.abes.findrc.domain.service.ReferenceContextuelServiceDebug;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/")
public class FindRcController {

    private final ReferenceContextuelService referenceContextuelService;

    private final ReferenceContextuelServiceDebug referenceContextuelServiceDebug;


    @GetMapping("req")
    @Operation(description = "A partir d'une appellation (dont la forme la plus courante est firstName / lastName), le service renvoie les points d’accès des notices bibliographiques Sudoc non liés à une autorité de la base IdRef (sous la forme ppn + \"-\" + sa position à partir des zones 70X ; ex. 123456789-1).  <br/><br/>" +
            "Avec le paramètre \"file\", il est possible d’appeler un fichier de requêtes en particulier, afin d’ajuster les requêtes passées à l’appellation en entrée (ex : réduite à un lastName) ou le degré de finesse des résultats (ex : recherche étroite).  <br/><br/>" +
            "Ce service est le complémentaire du service \"linked_rc_idref_sudoc\".",
            parameters = {
                    @Parameter(name = "firstName", in = ParameterIn.QUERY, required = false, example = "valérie", description = "Prénom"),
                    @Parameter(name = "lastName", in = ParameterIn.QUERY, required = true, example = "robert", description = "Nom"),
                    @Parameter(name = "file", in = ParameterIn.QUERY, required = false, description = "Fichier de requêtes (ex : findrc_light)"),
                    @Parameter(name = "format", in = ParameterIn.QUERY, required = false, description = "Format de la réponse : xml, json (défaut)")
            }
    )
    public Mono<ReferenceAutoriteGetDto> getAll(@RequestParam(required = false) String file,
                                                @RequestParam(value="firstName", required = false) String firstName,
                                                @RequestParam(value="lastName") String lastName
                                                ) {

        log.info("Connect to Findra Service");

        String getFile = null;
        if (!Strings.isNullOrEmpty(file)) {
            getFile = file;
        } else {
            getFile = "rc_full";
        }

        if (firstName == null) {
            firstName = "";
        }

        if (!Strings.isNullOrEmpty(firstName) && (firstName.charAt(0) == '*' || lastName.charAt(0) == '*')) {
            ReferenceAutoriteGetDto referenceAutoriteGetDto = new ReferenceAutoriteGetDto(0, null, getFile,new ArrayList<>());
            return Mono.just(referenceAutoriteGetDto);

        } else {

            return referenceContextuelService.findAllRCFromDatabase(getFile,firstName,lastName);
        }

    }

    @GetMapping("debug/req")
    @Operation(description = "Mode debug : à partir d'une appellation (dont la forme la plus courante est firstName / lastName), le service renvoie les points d’accès des notices bibliographiques Sudoc non liés à une autorité de la base IdRef (sous la forme ppn + \"-\" + sa position à partir des zones 70X ; ex. 123456789-1).  <br/><br/>" +
            "Avec le paramètre \"file\", il est possible d’appeler un fichier de requêtes en particulier, afin d’ajuster les requêtes passées à l’appellation en entrée (ex : réduite à un lastName) ou le degré de finesse des résultats (ex : recherche étroite).  <br/><br/>" +
            "Ce service est le complémentaire du service \"linked_rc_idref_sudoc\".",
            parameters = {
                    @Parameter(name = "firstName", in = ParameterIn.QUERY, required = false, example = "valérie", description = "Prénom"),
                    @Parameter(name = "lastName", in = ParameterIn.QUERY, required = true, example = "robert", description = "Nom"),
                    @Parameter(name = "file", in = ParameterIn.QUERY, required = false, description = "Fichier de requêtes (ex : findrc_light)")
            }
    )
    public Flux<ReferenceAutoriteDtoDebugProxy> getAllDebug(@RequestParam(required = false) String file,
                                                            @RequestParam(value="firstName", required = false) String firstName,
                                                            @RequestParam(value="lastName") String lastName
    ) {

        log.info("Connect to Findra Service");

        String getFile = null;
        if (!Strings.isNullOrEmpty(file)) {
            getFile = file;
        } else {
            getFile = "rc_full";
        }

        if (firstName == null) {
            firstName = "";
        }

        //ACT 27/06/24 : on accepte * en firstName
        //if (!Strings.isNullOrEmpty(firstName) && (firstName.charAt(0) == '*' || lastName.charAt(0) == '*')) {

        if (!Strings.isNullOrEmpty(firstName) && (lastName.charAt(0) == '*')) {
            ReferenceAutoriteDtoDebugProxy referenceAutoriteDtoDebugProxy = new ReferenceAutoriteDtoDebugProxy();
            return Flux.just(referenceAutoriteDtoDebugProxy);

        } else {

            return referenceContextuelServiceDebug.findAllRCDebugModeFromFindRA(getFile,firstName,lastName);
        }

    }

}
