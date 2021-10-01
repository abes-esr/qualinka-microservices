package fr.abes.partitionInitiale.domain.repository;

import fr.abes.partitionInitiale.domain.dto.ReferenceAutoriteDtoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="findrc")
public interface ReferenceContextuelProxy {

    @GetMapping("api/v2/req")
    Mono<ReferenceAutoriteDtoProxy> findrcExchangeProxy(@RequestParam(value="prenom") String firstName,
                                                        @RequestParam(value="nom") String lastName);
}
