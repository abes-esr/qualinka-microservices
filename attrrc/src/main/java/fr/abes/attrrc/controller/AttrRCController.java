package fr.abes.attrrc.controller;

import fr.abes.attrrc.domain.dto.RCDto;
import fr.abes.attrrc.domain.service.AttrRCService;
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
public class AttrRCController {

    private final AttrRCService attrRCService;
    private final ResourceLoader resourceLoader;


    @GetMapping("req")
    public Mono<RCDto> getAll(@RequestParam(value="ppn") String ppn) {

        log.info("Connect to AttrRC Service");

        return attrRCService.attributs(ppn);


    }

}
