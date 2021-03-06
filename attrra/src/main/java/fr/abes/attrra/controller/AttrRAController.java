package fr.abes.attrra.controller;

import fr.abes.attrra.domain.dto.RADto;
import fr.abes.attrra.domain.service.AttrRAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/")
public class AttrRAController {

    private final AttrRAService attrRAService;

    @GetMapping("req")
    @Operation(description = "A partir d'un identifiant IdRef d'autorité (sous la forme ex. 123456879), le service renvoie des informations issues des champs de la notice d’autorité sous la forme d’attributs.",
            parameters = {
                    @Parameter(name = "ra_id", in = ParameterIn.QUERY, required = true, example = "076642860", description = "Identifiant IdRef d'autorité"),
                    @Parameter(name = "format", in = ParameterIn.QUERY, required = false, description = "Format de la réponse : xml, json (défaut)")
            }
    )
    public Mono<RADto> getAll(
                             @RequestParam(value="ra_id") String ra_id
                            ) {

        log.info("Connect to AttrRA Service");
        return attrRAService.attributs(ra_id);
    }

}
