package fr.cytech.projetdevwebbackend.users.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.cytech.projetdevwebbackend.users.dto.UsernameDto;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.service.UserAdministrationService;
import fr.cytech.projetdevwebbackend.errors.types.UserAdministrationError;
import fr.cytech.projetdevwebbackend.util.Either;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for user administration operations.
 * <p>
 * This controller provides endpoints for administrative actions on user
 * accounts,
 * such as accepting pending users. All endpoints require administrative
 * privileges.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserAdministrationController {

    private final UserAdministrationService userAdministrationService;

    /**
     * Creates a new user administration controller with required dependencies.
     *
     * @param userAdministrationService The service handling user administration
     *                                  logic
     */
    @Autowired
    public UserAdministrationController(UserAdministrationService userAdministrationService) {
        this.userAdministrationService = userAdministrationService;
    }

    /**
     * Accepts a pending user, granting them standard user privileges.
     * <p>
     * This endpoint moves a user from PENDING status to active USER status.
     * Requires ADMIN role.
     *
     * @param usernameDto DTO containing the username to accept
     * @return ResponseEntity with success or error status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/acceptuser")
    public ResponseEntity<?> acceptUser(@RequestBody @Valid UsernameDto usernameDto) {
        log.debug("Accepting user: {}", usernameDto.getUsername());

        if (usernameDto.getUsername().isBlank()) {
            log.warn("Attempt to accept user with blank username");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Bad Request");
            errorResponse.put("message", "Username cannot be blank");
            errorResponse.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            errorResponse.put("path", "/api/auth/acceptuser");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Either<UserAdministrationError, User> acceptationStatus = userAdministrationService
                .acceptUser(usernameDto.getUsername());

        return acceptationStatus.fold(
                // Handle error case
                error -> {
                    log.warn("Failed to accept user {}: {}", usernameDto.getUsername(), error);

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
                    errorResponse.put("error", "User Acceptance Failed");
                    errorResponse.put("message", error.getMessage());
                    errorResponse.put("errorCode", error.toString());
                    errorResponse.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    errorResponse.put("path", "/api/auth/acceptuser");

                    return ResponseEntity.badRequest().body(errorResponse);
                },
                // Handle success case
                user -> {
                    log.info("User accepted successfully: {}", usernameDto.getUsername());

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", HttpStatus.OK.value());
                    response.put("message", "User accepted successfully");
                    response.put("username", user.getUsername());
                    response.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                    return ResponseEntity.ok(response);
                });
    }
}
