package fr.abes.findrc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
@OpenAPIDefinition(info = @Info(title = "find-nonlinked-rc-sudoc", version = "2.0", description = "A partir d'un nom et prénom (name et lastname), renvoie les références contextuelles (RC) non liées (pas de 70X$3) dans la base de données Sudoc. Un fichier de requêtes Solr peut être précisé (file)"))
@EnableEncryptableProperties
public class FindrcApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindrcApplication.class, args);
	}

}
