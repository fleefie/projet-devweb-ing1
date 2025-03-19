package fr.cytech.projetdevwebbackend.users.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.cytech.projetdevwebbackend.users.dto.LoginDto;
import fr.cytech.projetdevwebbackend.users.dto.RegisterDto;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.service.AuthServiceImpl;
import fr.cytech.projetdevwebbackend.errors.types.AuthError;
import fr.cytech.projetdevwebbackend.users.jwt.JwtAuthResponse;
import fr.cytech.projetdevwebbackend.util.Either;
import jakarta.validation.Valid;

/**
 * REST controller for authentication operations.
 * <p>
 * This controller handles user registration and login functionality,
 * providing endpoints for user creation and authentication.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthServiceImpl authService;

    /**
     * Creates a new authentication controller with required dependencies.
     *
     * @param authService The service handling authentication logic
     */
    @Autowired
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginDto Login credentials (username/email and password)
     * @return JWT token wrapped in a response object or an error response
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        log.debug("Login attempt for user: {}", loginDto.getUsernameOrEmail());

        Either<AuthError, JwtAuthResponse> loginResult = authService.login(loginDto);
        return loginResult.fold(
                err -> {
                    log.warn("Login failed for user {}: {}", loginDto.getUsernameOrEmail(), err);

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", err.getMessage());

                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
                },
                response -> {
                    log.info("User logged in successfully: {}", loginDto.getUsernameOrEmail());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Registers a new user account.
     *
     * @param registerDto Registration information
     * @return Success confirmation or error details
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDto registerDto) {
        log.debug("Registration attempt for username: {}", registerDto.getUsername());

        // Check if passwords match
        if (!registerDto.getPassword().equals(registerDto.getPasswordConfirm())) {
            log.warn("Registration failed: passwords do not match for username: {}", registerDto.getUsername());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Passwords do not match");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        Either<AuthError, User> result = authService.register(
                registerDto.getUsername(),
                registerDto.getPassword(),
                registerDto.getEmail(),
                registerDto.getName(),
                true);

        return result.fold(
                // Handle error case
                error -> {
                    log.warn("Registration failed for username {}: {}", registerDto.getUsername(), error);

                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("message", error.getMessage());

                    return ResponseEntity.badRequest().body(errorResponse);
                },
                // Handle success case
                user -> {
                    log.info("User registered successfully: {}", user.getUsername());

                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "User registered successfully");

                    return ResponseEntity.ok(response);
                });
    }
}
