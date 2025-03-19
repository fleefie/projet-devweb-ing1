package fr.cytech.projetdevwebbackend.users.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.cytech.projetdevwebbackend.errors.types.AuthError;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.UserRepository;
import fr.cytech.projetdevwebbackend.util.Either;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom implementation of Spring Security's UserDetailsService and our
 * functional UserDetailsProvider.
 * <p>
 * This service loads user-specific data for authentication and authorization.
 * It provides both exception-based and functional error handling approaches.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService, UserDetailsProvider {

    private final UserRepository userRepository;

    /**
     * Creates a new CustomUserDetailsService with the required repository.
     *
     * @param userRepository Repository for user data access
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Implementation of Spring Security's UserDetailsService.
     * <p>
     * This method is required by Spring Security but delegates to our own impl.
     *
     * @param usernameOrEmail The username or email to load
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException If user is not found or account is not
     *                                   enabled
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Either<AuthError, UserDetails> result = findUserDetails(usernameOrEmail);

        if (result.isRight()) {
            return result.getRight();
        } else {
            throw new UsernameNotFoundException(result.getLeft().getMessage());
        }
    }

    /**
     * Better implementation of UserDetailsProvider.
     * <p>
     * Uses proper error handling instead of exceptions.
     *
     * @param usernameOrEmail The username or email to load
     * @return Either containing error information or user details
     */
    @Override
    public Either<AuthError, UserDetails> findUserDetails(String usernameOrEmail) {
        log.debug("Loading user details for: {}", usernameOrEmail);

        // Find user by username or email
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        if (userOptional.isEmpty()) {
            log.warn("Failed login attempt with non-existent user: {}", usernameOrEmail);
            return Either.left(AuthError.USER_DOES_NOT_EXIST);
        }

        User user = userOptional.get();

        // Check if email is verified
        if (!user.isVerified()) {
            log.warn("Login attempt by user with unverified email: {}", usernameOrEmail);
            return Either.left(AuthError.EMAIL_NOT_VALIDATED);
        }

        // Check if user has a PENDING role - users with PENDING role are not yet
        // accepted
        if (!user.isAccepted()) {
            log.warn("Login attempt by user with pending acceptance: {}", usernameOrEmail);
            return Either.left(AuthError.ACCOUNT_NOT_ACCEPTED);
        }

        // Other status checks based on your User implementation
        if (user.isLocked()) {
            log.warn("Login attempt by locked user account: {}", usernameOrEmail);
            return Either.left(AuthError.ACCOUNT_LOCKED);
        }

        // Map user roles to Spring Security authorities
        Set<GrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        log.debug("User {} loaded successfully with {} authorities", usernameOrEmail, authorities.size());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                !user.isLocked(),
                authorities);

        return Either.right(userDetails);
    }

    /**
     * Maps domain role entities to Spring Security GrantedAuthority objects.
     *
     * @param roles Set of application roles
     * @return Set of Spring Security authorities
     */
    private Set<GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
    }
}
