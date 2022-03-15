package fr.abes.findra;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/find-ra-idref/", description = "Default Server URL")},
                    info = @Info(title = "find-ra-idref", version = "2.0", description = "A partir d'un nom et prénom (name et lastname), renvoie les références d'autorités (RA) correspondantes dans la base de données IdRef. Un fichier de requêtes Solr peut être précisé (file)"))
@EnableEncryptableProperties
public class FindraApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindraApplication.class, args);
    }

}
