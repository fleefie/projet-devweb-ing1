package fr.cytech.projetdevwebbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProjetDevwebBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjetDevwebBackendApplication.class, args);
    }
}
