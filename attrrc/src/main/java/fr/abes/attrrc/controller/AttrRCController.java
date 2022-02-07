package fr.abes.attrrc.controller;

import fr.abes.attrrc.domain.dto.RCDto;
import fr.abes.attrrc.domain.service.AttrRCService;
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
    public Mono<RCDto> getAll(@RequestParam(value="rc_id") String rc_id) throws SQLException {

        log.info("Connect to AttrRC Service");

        return attrRCService.attributs(rc_id);

    }

}
