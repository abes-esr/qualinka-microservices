package fr.abes.linked_rc_idref_sudoc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/linked_rc_idref_sudoc/", description = "Default Server URL")},
                    info = @Info(title = "linked_rc_idref_sudoc", version = "2.0", description = "A partir d'un identifiant d'une RA (le ppn) de la base IdRef, renvoie les RC liées de la base Sudoc (en 70X$3)"))
@EnableEncryptableProperties
public class LinkedRc {

    public static void main(String[] args) {
        SpringApplication.run(LinkedRc.class, args);
    }

}
