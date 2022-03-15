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
    @Operation(description = "A partir d'un nom et prénom (name et lastname), renvoie les références contextuelles (RC) non liées (pas de 70X$3) dans la base de données Sudoc. Un fichier de requêtes Solr peut être précisé (file)",
            parameters = {
                    @Parameter(name = "firstName", in = ParameterIn.QUERY, required = false, example = "valérie", description = "Prénom"),
                    @Parameter(name = "lastName", in = ParameterIn.QUERY, required = true, example = "robert", description = "Nom"),
                    @Parameter(name = "file", in = ParameterIn.QUERY, required = false, example = "findrc_light", description = "Fichier de requêtes Solr"),
                    @Parameter(name = "format", in = ParameterIn.QUERY, required = false, description = "Format de la réponse : xml, json (défaut)")
            }
    )
    public Mono<ReferenceAutoriteGetDto> getAll(@RequestParam(required = false) String file,
                                                @RequestParam(required = false) String firstName,
                                                @RequestParam(value="lastName") String lastName
                                                ) {

        log.info("Connect to Findra Service");

        String getFile = null;
        if (!Strings.isNullOrEmpty(file)) {
            getFile = file;
        } else {
            getFile = "defaultv2-req-rc";
        }

        if (firstName == null) {
            firstName = "";
        }

        System.out.println(firstName);

        if (!Strings.isNullOrEmpty(firstName) && (firstName.charAt(0) == '*' || lastName.charAt(0) == '*')) {
            ReferenceAutoriteGetDto referenceAutoriteGetDto = new ReferenceAutoriteGetDto(0, null, getFile,new ArrayList<>());
            return Mono.just(referenceAutoriteGetDto);

        } else {

            return referenceContextuelService.findAllRCFromDatabase(getFile,firstName,lastName);
        }

    }

    @GetMapping("debug/req")
    @Operation(description = "Mode debug : à partir d'un nom et prénom (name et lastname), renvoie les références contextuelles (RC) non liées (pas de 70X$3) dans la base de données Sudoc. Un fichier de requêtes Solr peut être précisé (file)",
            parameters = {
                    @Parameter(name = "firstName", in = ParameterIn.QUERY, required = false, example = "valérie", description = "Prénom"),
                    @Parameter(name = "lastName", in = ParameterIn.QUERY, required = true, example = "robert", description = "Nom"),
                    @Parameter(name = "file", in = ParameterIn.QUERY, required = false, example = "findrc_light", description = "Fichier de requêtes Solr")
            }
    )
    public Flux<ReferenceAutoriteDtoDebugProxy> getAllDebug(@RequestParam(required = false) String file,
                                                            @RequestParam(value="firstName") String firstName,
                                                            @RequestParam(value="lastName") String lastName
    ) {

        log.info("Connect to Findra Service");
        String getFile = null;
        if (!Strings.isNullOrEmpty(file)) {
            getFile = file;
        }

        if (!Strings.isNullOrEmpty(firstName) && (firstName.charAt(0) == '*' || lastName.charAt(0) == '*')) {
            ReferenceAutoriteDtoDebugProxy referenceAutoriteDtoDebugProxy = new ReferenceAutoriteDtoDebugProxy();
            return Flux.just(referenceAutoriteDtoDebugProxy);

        } else {

            return referenceContextuelServiceDebug.findAllRCDebugModeFromFindRA(getFile,firstName,lastName);
        }

    }

}
