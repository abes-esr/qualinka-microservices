package fr.abes.attrra.controller;

import fr.abes.attrra.domain.dto.RADto;
import fr.abes.attrra.domain.service.AttrRAService;
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
public class AttrRAController {

    private final AttrRAService attrRAService;
    private final ResourceLoader resourceLoader;


    @GetMapping("req")
    public Mono<RADto> getAll(
                             @RequestParam(value="ppn") String ppn
                            ) {

        log.info("Connect to AttrRA Service");

        return attrRAService.attributs(ppn);


    }

}
