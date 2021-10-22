package fr.abes.attrrc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactivefeign.spring.config.EnableReactiveFeignClients;

import java.io.IOException;

@SpringBootApplication
@EnableReactiveFeignClients
@EnableEncryptableProperties
public class AttrRCApplication {

	public static void main(String[] args) {

		SpringApplication.run(AttrRCApplication.class, args);
	}

}
