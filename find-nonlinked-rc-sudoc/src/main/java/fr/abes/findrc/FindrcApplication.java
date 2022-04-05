package fr.abes.findrc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
@OpenAPIDefinition(servers = {@Server(url = "/find-nonlinked-rc-sudoc/", description = "Default Server URL")},
					info = @Info(title = "find-nonlinked-rc-sudoc", version = "2.0",
							description = "A partir d'une appellation (dont la forme la plus courante est firstName / lastName), le service renvoie les points d’accès des notices bibliographiques Sudoc non liés à une autorité de la base IdRef (sous la forme ppn + \"-\" + sa position à partir des zones 70X ; ex. 123456789-1).  <br/><br/>" +
							"Avec le paramètre \"file\", il est possible d’appeler un fichier de requêtes en particulier, afin d’ajuster les requêtes passées à l’appellation en entrée (ex : réduite à un lastName) ou le degré de finesse des résultats (ex : recherche étroite).  <br/><br/>" +
							"Ce service est le complémentaire du service \"linked_rc_idref_sudoc\"."))
@EnableEncryptableProperties
public class FindrcApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindrcApplication.class, args);
	}

}
