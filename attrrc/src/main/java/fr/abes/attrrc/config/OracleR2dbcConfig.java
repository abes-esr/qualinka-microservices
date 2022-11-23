package fr.abes.attrrc.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.DatabaseType;
import org.davidmoten.rx.jdbc.pool.NonBlockingConnectionPool;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class OracleR2dbcConfig {

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

    private Database db;

    @Bean
    public Database connectionFactory() throws SQLException {

        log.info("Connecting to database =>  jdbc:oracle:thin:@"+host+":"+port+"/"+serviceName);
        String url = "jdbc:oracle:thin:@"+host+":"+port+"/"+serviceName;

        NonBlockingConnectionPool pool =
                Pools.nonBlocking()
                        .connectionProvider(ConnectionProvider.from(url, user, password))
                        // an unused connection will be closed after thirty minutes
                        .maxIdleTime(5, TimeUnit.MINUTES)
                        // connections are checked for healthiness on checkout if the connection
                        // has been idle for at least 5 seconds
                        .healthCheck(DatabaseType.ORACLE)
                        .idleTimeBeforeHealthCheck(5, TimeUnit.SECONDS)
                        // if a connection fails creation then retry after 30 seconds
                        .connectionRetryInterval(30, TimeUnit.SECONDS)
                        // the maximum number of connections in the pool
                        //.maxPoolSize(Runtime.getRuntime().availableProcessors() * 5)
                        //Test :
                        .maxPoolSize(1)

                        .build();
        return Database.from(pool);
    }
}
