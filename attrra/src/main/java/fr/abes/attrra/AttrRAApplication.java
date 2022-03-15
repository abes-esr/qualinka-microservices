package fr.abes.attrra;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "attrra", version = "2.0", description = "A partir d'un identifiant d'une RA (le ppn), renvoie ses attributs"))
@EnableEncryptableProperties
public class AttrRAApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttrRAApplication.class, args);
	}

}
