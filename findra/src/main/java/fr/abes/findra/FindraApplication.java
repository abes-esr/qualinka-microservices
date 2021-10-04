package fr.abes.findra;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEncryptableProperties
public class FindraApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindraApplication.class, args);
    }

}
