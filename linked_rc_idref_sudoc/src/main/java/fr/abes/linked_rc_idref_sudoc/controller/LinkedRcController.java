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
    @Operation(description = "A partir d'un identifiant d'une RA (le ppn) de la base IdRef, renvoie les RC liées de la base Sudoc (en 70X$3)",
                parameters = {
                    @Parameter(name = "ra_id", in = ParameterIn.QUERY, required = true, example = "076642860", description = "Identifiant d'une RA (le ppn) de la base IdRef"),
                    @Parameter(name = "format", in = ParameterIn.QUERY, required = false, description = "Format de la réponse : xml, json (défaut)")
                }
    )
    public Mono<LinkedRcGetDto> getLinkedRc(
                                                @RequestParam(value="ra_id") String ra_id
                                                ) {

        return linkedRcService.findLinkedRc(ra_id);


    }

}
