package fr.cytech.projetdevwebbackend;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import fr.cytech.projetdevwebbackend.devices.model.Device;
import fr.cytech.projetdevwebbackend.devices.model.repository.DeviceRepository;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.RoleRepository;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.users.service.AuthServiceImpl;
import fr.cytech.projetdevwebbackend.users.service.UserAdministrationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@SpringBootApplication
@RestController
public class ProjetDevwebBackendApplication implements CommandLineRunner {
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserAdministrationService userAdministrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.admin-username}")
    private String adminUsername;

    @Value("${app.admin-password}")
    private String adminPassword;

    @Value("${app.admin-email}")
    private String adminEmail;

    /**
     * Main entry point for the application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // Early database dir creation
        try {
            Files.createDirectories(Path.of("database"));
            Files.createDirectories(Path.of("database/devices"));
        } catch (Exception e) {
            // Fuck you
        }
        SpringApplication.run(ProjetDevwebBackendApplication.class, args);
    }

    /**
     * Runs at application startup to handle initialization tasks:
     * 1. Provides a password encryption utility when the "encrypt" argument is
     * passed
     * 2. Creates an admin user if one doesn't exist already
     *
     * @param args Command line arguments passed to the application
     */
    @Override
    @Transactional
    public void run(String... args) {
        // Handle password encryption utility
        if (handlePasswordEncryption(args)) {
            return;
        }

        // Create admin user if it doesn't exist
        createAdminUserIfNeeded();

        // Testing area,
        // ### PUT YOUR TEMPORARY STUFF HERE ###
        Map<String, Object> test = new HashMap<String, Object>();
        test.put("arg1", "val1");
        test.put("arg2", "val2");
        Map<String, Object> arg3 = new HashMap<String, Object>();
        arg3.put("arg3.1", "val3.1");
        arg3.put("arg3.2", "val3.2");
        test.put("arg3", arg3);

        Device dev = new Device("Test Device");
        dev.setProperties(test);
        deviceRepository.save(dev);
    }

    /**
     * Handles the password encryption utility functionality.
     * When "encrypt" is passed as an argument followed by a password,
     * it will encrypt the password and output the result.
     *
     * @param args Command line arguments
     * @return true if encryption was performed, false otherwise
     */
    private boolean handlePasswordEncryption(String... args) {
        boolean encryptFlag = false;
        String inputPass = null;

        for (String arg : args) {
            if (encryptFlag) {
                inputPass = arg;
                break;
            }
            if (arg.equals("encrypt")) {
                encryptFlag = true;
            }
        }

        if (encryptFlag && inputPass != null) {
            System.out.println("\n\n\nENCRYPTED PASSWORD: < " +
                    passwordEncoder.encode(inputPass) +
                    " >\n\n\n");
            System.exit(0);
            return true;
        }

        return false;
    }

    /**
     * Creates an admin user with appropriate roles if one doesn't exist.
     * The admin credentials are loaded from application properties.
     */
    private void createAdminUserIfNeeded() {
        if (!authService.doesUserExist(adminUsername, adminEmail)) {
            try {
                // Create admin user
                User admin = authService.register(adminUsername, adminPassword,
                        adminEmail, "Administrator", false)
                        .getRight();

                // Set user as verified
                admin.setVerified(true);

                // Assign admin role
                Role adminRole = roleRepository.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
                adminRole = entityManager.merge(adminRole); // Ensure role is in managed state
                admin.addRole(adminRole);

                // Accept the user
                userAdministrationService.acceptUser(adminUsername);

                // Save changes
                userRepository.save(admin);
            } catch (Exception e) {
                // Log the error and rethrow - critical initialization failure
                throw new RuntimeException("Failed to create admin user", e);
            }
        }
    }
}
