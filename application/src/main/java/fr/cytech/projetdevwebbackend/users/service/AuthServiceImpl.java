package fr.cytech.projetdevwebbackend.users.service;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.cytech.projetdevwebbackend.errors.types.AuthError;
import fr.cytech.projetdevwebbackend.errors.types.Error;
import fr.cytech.projetdevwebbackend.errors.types.TokenError;
import fr.cytech.projetdevwebbackend.users.dto.LoginDto;
import fr.cytech.projetdevwebbackend.users.jwt.JwtAuthResponse;
import fr.cytech.projetdevwebbackend.users.jwt.JwtTokenProvider;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.RoleRepository;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.util.Either;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of authentication service handling login and registration
 * operations.
 * <p>
 * This service manages user authentication, including login validation and new
 * user registration.
 * It uses JWT tokens for authentication and implements functional error
 * handling.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    /**
     * Validation constants
     */
    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final int MIN_PASSWORD_LENGTH = 15;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates an authentication service with required dependencies.
     *
     * @param authenticationManager Manager for authentication operations
     * @param userRepository        Repository for user data access
     * @param roleRepository        Repository for role management
     * @param jwtTokenProvider      Provider for JWT token operations
     */
    @Autowired
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Authenticates a user and provides a JWT token upon successful login.
     *
     * @param loginDto DTO containing login credentials
     * @return Either containing an error or the JWT authentication response
     */
    @Override
    public Either<AuthError, JwtAuthResponse> login(LoginDto loginDto) {
        log.debug("Processing login for user: {}", loginDto.getUsernameOrEmail());

        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsernameOrEmail(),
                            loginDto.getPassword()));

            // Check if user has PENDING authority
            boolean hasPendingAuthority = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_PENDING".equals(authority.getAuthority()) ||
                            "PENDING".equals(authority.getAuthority()));

            if (hasPendingAuthority) {
                log.warn("Login denied for user: {} - Account pending verification", loginDto.getUsernameOrEmail());
                return Either.left(AuthError.ACCOUNT_NOT_ACCEPTED);
            }

            // Set the authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            log.info("User logged in successfully: {}", loginDto.getUsernameOrEmail());
            return Either.right(JwtAuthResponse.withToken(token));

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for user: {} - Invalid credentials", loginDto.getUsernameOrEmail());
            return Either.left(AuthError.INVALID_CREDENTIALS);
        } catch (LockedException e) {
            log.warn("Failed login attempt for user: {} - Account locked", loginDto.getUsernameOrEmail());
            return Either.left(AuthError.ACCOUNT_LOCKED);
        } catch (DisabledException e) {
            log.warn("Failed login attempt for user: {} - Account disabled", loginDto.getUsernameOrEmail());
            return Either.left(AuthError.ACCOUNT_DISABLED);
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            return Either.left(AuthError.AUTHENTICATION_ERROR);
        }
    }

    /**
     * Checks if a user with the given username or email already exists.
     *
     * @param username Username to check
     * @param email    Email to check
     * @return true if user exists, false otherwise
     */
    @Override
    public boolean doesUserExist(String username, String email) {
        return userRepository.existsByUsername(username) || userRepository.existsByEmail(email);
    }

    /**
     * Registers a new user account with validation.
     *
     * @param username Username for the new account
     * @param password Password for the new account
     * @param email    Email address for the new account
     * @param name     Display name for the new account
     * @param doHash   Whether to hash the password (false only for testing)
     * @return Either containing an error or the created user
     */
    @Override
    @Transactional
    public Either<AuthError, User> register(String username, String password, String email,
            String name, Boolean doHash) {
        log.debug("Processing registration for username: {}, email: {}", username, email);

        // Empty field validation
        if (StringUtils.isBlank(username)) {
            return Either.left(AuthError.EMPTY_USERNAME);
        }
        if (StringUtils.isBlank(password)) {
            return Either.left(AuthError.EMPTY_PASSWORD);
        }
        if (StringUtils.isBlank(email)) {
            return Either.left(AuthError.EMPTY_EMAIL);
        }
        if (StringUtils.isBlank(name)) {
            return Either.left(AuthError.EMPTY_NAME);
        }

        // Format validation
        if (!Pattern.compile(EMAIL_REGEX).matcher(email).matches()) {
            return Either.left(AuthError.INVALID_EMAIL_FORMAT);
        }
        if (!StringUtils.isAlphanumeric(username)) {
            return Either.left(AuthError.USERNAME_NOT_ALPHANUMERIC);
        }
        if (!StringUtils.isAlphanumericSpace(name)) {
            return Either.left(AuthError.NAME_NOT_ALPHANUMERIC_SPACE);
        }
        if (!StringUtils.isAsciiPrintable(password)) {
            return Either.left(AuthError.PASSWORD_NOT_ASCII);
        }
        if (StringUtils.isAlphanumericSpace(password)) {
            return Either.left(AuthError.PASSWORD_TOO_SIMPLE);
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return Either.left(AuthError.PASSWORD_TOO_SHORT);
        }

        // Check if user exists
        if (userRepository.existsByUsername(username)) {
            return Either.left(AuthError.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(email)) {
            return Either.left(AuthError.EMAIL_ALREADY_EXISTS);
        }

        // Get pending role
        Role pendingRole = roleRepository.findByName(Role.ROLE_PENDING)
                .orElse(null);

        if (pendingRole == null) {
            log.error("Cannot register user: PENDING role not found");
            return Either.left(AuthError.ROLE_NOT_FOUND);
        }

        // Hash password if needed
        String passwordHashed = doHash ? passwordEncoder.encode(password) : password;

        // Create and save user
        User user = new User(name, username, email, passwordHashed, false);
        user.addRole(pendingRole);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", username);
        return Either.right(savedUser);
    }

    /**
     * Returns the current user from the provided JWT token.
     */
    public Either<Error, User> getUserFromToken(String token) {
        log.debug("Fetching user from token: {}", token);

        // Validate and parse the token
        if (!jwtTokenProvider.validateToken(token)) {
            return Either.left(TokenError.INVALID_TOKEN);
        }

        String username = jwtTokenProvider.extractUsername(token).fold(
                error -> {
                    log.error("Failed to extract username from token: {}", error);
                    return null;
                },
                usernameResult -> {
                    return usernameResult;
                });

        if (username == null) {
            return Either.left(TokenError.INVALID_TOKEN);
        }

        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return Either.left(AuthError.USER_DOES_NOT_EXIST);
        }

        return Either.right(user);
    }
}
