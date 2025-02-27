package fr.cytech.projet_devweb_ing1;

import fr.cytech.projet_devweb_ing1.model.User;
import fr.cytech.projet_devweb_ing1.model.User.UserType;
import fr.cytech.projet_devweb_ing1.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Runner when starting the Spring app.
 */
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        System.out.println("Populating database...");

        if (userRepository.findByUsername("admin").isEmpty())
            userRepository.save(new User("admin", passwordEncoder.encode("admin"), UserType.ADMIN));

        System.out.println("Application started!");
    }
}
