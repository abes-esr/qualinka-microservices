package fr.abes.findrc.domain.repository;


import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoDebugProxy;
import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name="find-ra-idref")
public interface ReferenceAutoriteProxy {

    @GetMapping("api/v2/req")
    Mono<ReferenceAutoriteDtoProxy> findraExchangeProxy(@RequestParam String from,
                                                        @RequestParam String file,
                                                        @RequestParam(value="firstName") String firstName,
                                                        @RequestParam(value="lastName") String lastName);


    @GetMapping("api/v2/debug/req")
    Flux<ReferenceAutoriteDtoDebugProxy> findraExchangeDebugProxy(@RequestParam String from,
                                                                  @RequestParam String file,
                                                                  @RequestParam(value="firstName") String firstName,
                                                                  @RequestParam(value="lastName") String lastName);


}

