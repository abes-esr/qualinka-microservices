package fr.abes.attrrc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/attrrc/", description = "Default Server URL")},
					info = @Info(title = "attrrc", version = "2.0", description = "A partir d’un identifiant Sudoc de point d’accès de notice bibliographique (sous la forme ppn + \"-\" + sa position à partir des zones 70X ; ex. 123456789-1), le service renvoie des informations issues des champs de la notice bibliographique sous la forme d’attributs."))
@EnableEncryptableProperties
public class AttrRCApplication {

	public static void main(String[] args) {

		SpringApplication.run(AttrRCApplication.class, args);
	}

}
