package fr.abes.linked_rc_idref_sudoc.controller;

import com.google.common.base.Strings;
import fr.abes.linked_rc_idref_sudoc.domain.dto.LinkedRcGetDto;
import fr.abes.linked_rc_idref_sudoc.domain.service.LinkedRcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Mono<LinkedRcGetDto> getLinkedRc(
                                                @RequestParam(value="ra_id") String ra_id
                                                ) {

        return linkedRcService.findLinkedRc(ra_id);


    }

}
