package fr.cytech.projetdevwebbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import fr.cytech.projetdevwebbackend.auth.model.User;
import fr.cytech.projetdevwebbackend.auth.model.repository.RoleRepository;
import fr.cytech.projetdevwebbackend.auth.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.auth.service.*;

@SpringBootApplication
@RestController
public class ProjetDevwebBackendApplication implements CommandLineRunner {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    UserAdministrationService userAdministrationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Value("${app.admin-username}")
    String adminUsername;
    @Value("${app.admin-password}")
    String adminPassword;
    @Value("${app.admin-email}")
    String adminEmail;

    public static void main(String[] args) {
        SpringApplication.run(ProjetDevwebBackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Boolean i = false;
        String inputPass = null;
        for (String arg : args) {
            if (i) {
                inputPass = arg;
                break;
            }
            if (arg.equals("encrypt"))
                i = true;
        }
        if (i) {
            System.out.println(
                    "\n\n\nENCRYPTED PASSWORD: < "
                            + passwordEncoder.encode(inputPass)
                            + " >\n\n\n");
            System.exit(0);
        }

        if (!authService.doesUserExists(adminUsername, adminEmail)) {
            // If this throws the app should panic anyways
            User admin = authService.register(adminUsername, adminPassword, adminEmail, "Administrator", false)
                    .getRight();
            admin.setVerified(true);
            admin.addRole(roleRepository.findByName("ADMIN").get());
            userAdministrationService.acceptUser(adminUsername);
            userRepository.save(admin);
        }
    }
}
