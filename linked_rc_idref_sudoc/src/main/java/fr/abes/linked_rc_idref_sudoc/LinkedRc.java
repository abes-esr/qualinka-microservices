package fr.abes.linked_rc_idref_sudoc;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class LinkedRc {

    public static void main(String[] args) {
        SpringApplication.run(LinkedRc.class, args);
    }

}
