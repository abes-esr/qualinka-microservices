package fr.abes.partitionInitiale.controller;

import com.google.common.base.Strings;
import fr.abes.partitionInitiale.domain.dto.PartitionInitialeDto;
import fr.abes.partitionInitiale.domain.dto.ReferenceAutoriteDtoProxy;
import fr.abes.partitionInitiale.domain.service.PartitionInitialeService;
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
public class PartitionInitialeController {

    private final PartitionInitialeService partitionInitialeService;
    private final ResourceLoader resourceLoader;


    @GetMapping("req")
    public Mono<PartitionInitialeDto> getAll(@RequestParam(required = false) String file,
                                             @RequestParam(value="prenom") String firstName,
                                             @RequestParam(value="nom") String lastName
                                                ) {

        log.info("Connect to PartitionInitiale Service");
        if (firstName.charAt(0) == '*' || lastName.charAt(0) == '*') {
            PartitionInitialeDto partitionInitialeDto = new PartitionInitialeDto("","",new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
            return Mono.just(partitionInitialeDto);

        } else {
            /*String getFile;
            if (!Strings.isNullOrEmpty(file)) {
                getFile = file;
            } else {
                getFile = "default-req-rc";
                log.info("Loading propertie file => {}", getFile);
            }*/

            return partitionInitialeService.partition(firstName,lastName);
        }

    }

}
