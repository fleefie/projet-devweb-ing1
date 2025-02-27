package fr.cytech.projet_devweb_ing1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProjetDevwebIng1Application {
    public static void main(String[] args) {
        SpringApplication.run(ProjetDevwebIng1Application.class, args);
    }
}
