package fr.abes.findra.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
public class BeanConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {

        log.info("Initializing WebClient Bean");

        return WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024))
                .build())
            .clientConnector(
            new ReactorClientHttpConnector(HttpClient.create()  // <== Create new reactor client for the connection
                .compress(true)
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                //.resolver(spec -> spec.queryTimeout(Duration.ofMillis(500)).trace("DNS", LogLevel.DEBUG)) // <== Bypass resolve DNS
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000) // <== Wait server response TCP connection Client/Server
                .responseTimeout(Duration.ofSeconds(5))  // <== Wait server response after send packet
                .doOnConnected(connection -> connection
                    .addHandlerLast(new ReadTimeoutHandler(10))
                    .addHandlerLast(new WriteTimeoutHandler(10))
                )
            )
        );
    }

}
