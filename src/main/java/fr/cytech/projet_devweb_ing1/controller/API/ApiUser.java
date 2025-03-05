package fr.cytech.projet_devweb_ing1.controller.API;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.cytech.projet_devweb_ing1.dto.UserSearchDTO;
import fr.cytech.projet_devweb_ing1.model.User;
import fr.cytech.projet_devweb_ing1.service.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

/**
 * API for user search and actions.
 */
@RestController
@RequestMapping("/api/users")
public class ApiUser {

    @Autowired
    private UserRepository userRepository;

    /**
     * Search mapping. Returns a list of users according to UserSearchDTO.
     * Accepts a blank search as a search of everything.
     */
    @PostMapping
    public ResponseEntity<?> getUsers(@RequestParam(value = "username", required = false) String query,
            HttpServletRequest request) {

        List<UserSearchDTO> users = (query == null)
                ? userRepository.findByTypeIn(List.of(User.UserType.ADMIN, User.UserType.COMPLEX))
                : userRepository.searchByUsername(query);

        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    /**
     * Action mapping. This is a template that should be used further later.
     */
    @PostMapping("/action")
    public ResponseEntity<?> handleAction(@RequestParam("id") Integer userId, HttpServletRequest request) {

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        System.out.println("Action lancée pour utilisateur ID " + userId);

        // TODO: Actual processing of the request
        return ResponseEntity.ok("Action executed for user ID: " + userId);
    }
}
