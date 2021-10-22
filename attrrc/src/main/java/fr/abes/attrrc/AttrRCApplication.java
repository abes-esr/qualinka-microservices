package fr.abes.attrrc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableEncryptableProperties
public class AttrRCApplication {

	public static void main(String[] args) {

		SpringApplication.run(AttrRCApplication.class, args);
	}

}
