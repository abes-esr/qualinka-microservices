package fr.abes.linked_rc_idref_sudoc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/linked_rc_idref_sudoc/", description = "Default Server URL")},
                    info = @Info(title = "linked_rc_idref_sudoc", version = "2.0",
                            description = "A partir d'un identifiant IdRef de notice d’autorité (sous la forme ex. 123456879), le service renvoie les points d’accès des notices bibliographiques Sudoc liés à une autorité de la base IdRef (sous la forme ppn + \"-\" + sa position à partir des zones 70X ; ex. 123456789-1).  <br/><br/>" +
                            "Avec le paramètre \"file\", il est possible d’appeler un fichier de requêtes en particulier, afin d’ajuster les requêtes passées à l’appellation en entrée (ex : réduite à un lastName) ou le degré de finesse des résultats (ex : recherche étroite).  <br/><br/>" +
                            "Ce service est le complémentaire du service \"find-nonlinked-rc-sudoc\"."))
@EnableEncryptableProperties
public class LinkedRc {

    public static void main(String[] args) {
        SpringApplication.run(LinkedRc.class, args);
    }

}
