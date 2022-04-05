package fr.abes.attrrc.controller;

import fr.abes.attrrc.domain.dto.RCDto;
import fr.abes.attrrc.domain.service.AttrRCService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.sql.SQLException;

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/")
public class AttrRCController {

    private final AttrRCService attrRCService;


    @GetMapping("req")
    @Operation(description = "A partir d’un identifiant Sudoc de point d’accès de notice bibliographique (sous la forme ppn + \"-\" + sa position à partir des zones 70X ; ex. 123456789-1), le service renvoie des informations issues des champs de la notice bibliographique sous la forme d’attributs.",
            parameters = {
                    @Parameter(name = "rc_id", in = ParameterIn.QUERY, required = true, example = "019057547-1", description = "Identifiant Sudoc de point d’accès de notice bibliographique"),
                    @Parameter(name = "format", in = ParameterIn.QUERY, required = false, description = "Format de la réponse : xml, json (défaut)")
            }
    )
    public Mono<RCDto> getAll(@RequestParam(value="rc_id") String rc_id) throws SQLException {

        log.info("Connect to AttrRC Service");

        return attrRCService.attributs(rc_id);

    }

}
