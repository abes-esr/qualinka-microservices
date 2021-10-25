package fr.abes.findrc.controller;

import com.google.common.base.Strings;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDto;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoProxy;
import fr.abes.findrc.domain.service.ReferenceContextuelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.ArrayList;

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/")
public class FindRcController {

    private final ReferenceContextuelService referenceContextuelService;


    @GetMapping("req")
    public Mono<ReferenceAutoriteDto> getAll(@RequestParam(required = false) String file,
                                             @RequestParam(value="prenom") String firstName,
                                             @RequestParam(value="nom") String lastName
                                                ) {

        log.info("Connect to Findra Service");
        String getFile;
        if (!Strings.isNullOrEmpty(file)) {
            getFile = file;
        } else {
            getFile = "default-req-rc";
            log.info("Loading propertie file => {}", getFile);
        }

        if (firstName.charAt(0) == '*' || lastName.charAt(0) == '*') {
            ReferenceAutoriteDto referenceAutoriteGetDto = new ReferenceAutoriteDto(0, null, getFile,new ArrayList<>());
            return Mono.just(referenceAutoriteGetDto);

        } else {

            return referenceContextuelService.findAllRCFromDatabase(getFile,firstName,lastName);
        }

    }

}
