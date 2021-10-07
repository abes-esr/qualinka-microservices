package fr.abes.linked_rc_idref_sudoc.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = {"fr.abes.linked_rc_idref_sudoc.domain.repository"})
@Slf4j
public class OracleR2dbcConfig extends AbstractR2dbcConfiguration {

    @Value("${database.host}")
    private String host;

    @Value("${database.port}")
    private int port;

    @Value("${database.serviceName}")
    private String serviceName;

    @Value("${database.user}")
    private String user;

    @Value("${database.password}")
    private String password;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {

        /*String descriptor = "(DESCRIPTION=" +
                "(ADDRESS=(HOST=" + host + ")(PORT=" + port + ")(PROTOCOL=tcp))" +
                "(CONNECT_DATA=(SERVICE_NAME=" + serviceName + ")))";

        log.info("Creating connection factory with descriptor " + descriptor);

        String r2dbcUrl = "r2dbc:oracle://?oracleNetDescriptor="+descriptor;

        String url = String.format("r2dbc:oracle:thin://%s:%d/%s", host, port, serviceName);


        log.info("Creating connection factory with descriptor " + url);*/

        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "oracle")
                .option(ConnectionFactoryOptions.HOST, host)
                .option(ConnectionFactoryOptions.PORT, 1521)
                .option(ConnectionFactoryOptions.DATABASE, serviceName)
                .option(ConnectionFactoryOptions.USER, user)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                .build());
    }
}
