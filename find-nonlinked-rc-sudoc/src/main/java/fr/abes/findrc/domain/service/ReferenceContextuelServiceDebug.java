package fr.abes.findrc.domain.service;

import fr.abes.findrc.domain.dto.ReferenceAutoriteDtoDebugProxy;
import fr.abes.findrc.domain.repository.ReferenceAutoriteOracle;
import fr.abes.findrc.domain.repository.ReferenceAutoriteProxy;
import fr.abes.findrc.domain.utils.MapStructMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReferenceContextuelServiceDebug {

    private final ReferenceAutoriteOracle referenceAutoriteOracle;
    private final WebClient.Builder webClientBuilder;
    private final ReferenceAutoriteProxy referenceAutoriteProxy;
    private final MapStructMapper mapStructMapper;


    // This method returns filter function which will log request data
    // Using this for DEBUG mod
    private static ExchangeFilterFunction logRequestWebclient() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    public Flux<ReferenceAutoriteDtoDebugProxy> findAllRCDebugModeFromFindRA(String fileName, String firstName, String lastName) {

        return Flux.defer(() -> referenceAutoriteProxy.findraExchangeDebugProxy("fromFindrc", fileName, firstName, lastName))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                .doOnError(e -> log.warn("Can not fetch info from Findra service"))
                .onErrorResume(t -> Flux.empty())
                .switchIfEmpty(v -> Mono.just(new ReferenceAutoriteDtoDebugProxy()));

    }

}
