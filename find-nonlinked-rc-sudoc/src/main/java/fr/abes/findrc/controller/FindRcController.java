package fr.abes.findrc.controller;

import com.google.common.base.Strings;
import fr.abes.findrc.domain.dto.ReferenceAutoriteGetDto;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoDebugProxy;
import fr.abes.findrc.domain.service.ReferenceContextuelService;
import fr.abes.findrc.domain.service.ReferenceContextuelServiceDebug;
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
    public Mono<ReferenceAutoriteGetDto> getAll(@RequestParam(required = false) String file,
                                                @RequestParam(value="firstName") String firstName,
                                                @RequestParam(value="lastName") String lastName
                                                ) {

        log.info("Connect to Findra Service");

        String getFile = null;
        if (!Strings.isNullOrEmpty(file)) {
            getFile = file;
        }

        if (!Strings.isNullOrEmpty(firstName) && (firstName.charAt(0) == '*' || lastName.charAt(0) == '*')) {
            ReferenceAutoriteGetDto referenceAutoriteGetDto = new ReferenceAutoriteGetDto(0, null, getFile,new ArrayList<>());
            return Mono.just(referenceAutoriteGetDto);

        } else {

            return referenceContextuelService.findAllRCFromDatabase(getFile,firstName,lastName);
        }

    }

    @GetMapping("debug/req")
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
