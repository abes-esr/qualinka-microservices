package fr.abes.findra.controller;

import com.google.common.base.Strings;
import fr.abes.findra.domain.dto.ReferenceAutoriteGetDto;
import fr.abes.findra.domain.dto.ReferenceAutoriteGetDtoModeDebug;
import fr.abes.findra.domain.service.ReferenceAutoriteModeDebugService;
import fr.abes.findra.domain.service.ReferenceAutoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/")
public class FindRaController {

    private final ReferenceAutoriteService referenceAutoriteService;
    private final ReferenceAutoriteModeDebugService referenceAutoriteModeDebugService;
    private final ResourceLoader resourceLoader;


    @GetMapping("req")
    public Mono<ReferenceAutoriteGetDto> getAllRa(@RequestParam(required = false) String from,
                                                @RequestParam(required = false) String file,
                                                @RequestParam(value="firstName") String firstName,
                                                @RequestParam(value="lastName") String lastName
                                                ) {

        String getFile;

        if (!Strings.isNullOrEmpty(file)) {

            //Resource resource = resourceLoader.getResource("classpath:solr-requetes/"+file+".properties");

            String urlGit = "https://raw.githubusercontent.com/abes-esr/qualinka-findws-requests/master/"+ file+".properties";
            Resource resource = resourceLoader.getResource(urlGit);

            if (resource.exists()) {

                getFile = file;
                log.info("Loading propertie file => {}", getFile);
            } else {

                if (!Strings.isNullOrEmpty(from) && from.equals("fromFindrc")) {
                    getFile = "defaultv2-req-rc";
                } else {
                    getFile = "defaultv2-req";
                }
                log.warn("Can not found the file with name {}, loading default file : {}", file, getFile);
            }

        } else {
            if (!Strings.isNullOrEmpty(from) && from.equals("fromFindrc")) {
                getFile = "defaultv2-req-rc";
            } else {
                getFile = "defaultv2-req";
            }
            log.info("loading default file : {}", getFile);
        }

        return referenceAutoriteService.findAllRA(from,getFile,firstName,lastName);


    }


    @GetMapping(value="debug/req", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ReferenceAutoriteGetDtoModeDebug> getAllRaAsModeDebug(@RequestParam(required = false) String from,
                                            @RequestParam(required = false) String file,
                                            @RequestParam(value="firstName") String firstName,
                                            @RequestParam(value="lastName") String lastName
    ) {

        String getFile;

        if (!Strings.isNullOrEmpty(file)) {

            //Resource resource = resourceLoader.getResource("classpath:solr-requetes/"+file+".properties");

            String urlGit = "https://raw.githubusercontent.com/abes-esr/qualinka-findws-requests/master/"+ file+".properties";
            Resource resource = resourceLoader.getResource(urlGit);

            if (resource.exists()) {

                getFile = file;
                log.info("Loading propertie file => {}", getFile);
            } else {

                if (!Strings.isNullOrEmpty(from) && from.equals("fromFindrc")) {
                    getFile = "defaultv2-req-rc";
                } else {
                    getFile = "defaultv2-req";
                }
                log.warn("Can not found the file with name {}, loading default file : {}", file, getFile);
            }

        } else {
            if (!Strings.isNullOrEmpty(from) && from.equals("fromFindrc")) {
                getFile = "defaultv2-req-rc";
            } else {
                getFile = "defaultv2-req";
            }
            log.info("loading default file : {}", getFile);
        }

        return referenceAutoriteModeDebugService.findAllRAAsModeDebug(from,getFile,firstName,lastName);


    }

}
