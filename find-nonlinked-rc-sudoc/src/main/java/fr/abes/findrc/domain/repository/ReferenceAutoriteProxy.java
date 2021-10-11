package fr.abes.findrc.domain.repository;


import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="find-ra-idref")
public interface ReferenceAutoriteProxy {

    @GetMapping("api/v2/req")
    Mono<ReferenceAutoriteDtoProxy> findraExchangeProxy(@RequestParam String from,
                                                        @RequestParam String file,
                                                        @RequestParam(value="prenom") String firstName,
                                                        @RequestParam(value="nom") String lastName);


}

