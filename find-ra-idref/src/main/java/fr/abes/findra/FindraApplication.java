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
                    info = @Info(title = "find-ra-idref", version = "2.0",
                            description = "A partir d'une appellation (dont la forme la plus courante est firstName / lastName), le service renvoie les notices d’autorité de la base IdRef.  <br/><br/>" +
                                    "Avec le paramètre \"file\", il est possible d’appeler un fichier de requêtes en particulier, afin d’ajuster les requêtes passées à l’appellation en entrée (ex : réduite à un lastName) ou le degré de finesse des résultats (ex : recherche étroite)."))
@EnableEncryptableProperties
public class FindraApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindraApplication.class, args);
    }

}
