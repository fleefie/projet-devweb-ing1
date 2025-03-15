package fr.cytech.projetdevwebbackend.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import fr.cytech.projetdevwebbackend.users.jwt.JwtAuthenticationEntryPoint;
import fr.cytech.projetdevwebbackend.users.jwt.JwtAuthenticationFilter;
import fr.cytech.projetdevwebbackend.users.service.CustomUserDetailsService;

/**
 * Spring Security configuration class that sets up JWT-based authentication.
 * <p>
 * This configuration:
 * <ul>
 * <li>Disables CSRF protection (typical for stateless REST APIs)</li>
 * <li>Configures public endpoints that don't require authentication</li>
 * <li>Sets up JWT-based authentication with stateless sessions</li>
 * <li>Configures the authentication provider and error handling</li>
 * </ul>
 * 
 * @since 2025-03-15
 * @author fleefie
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Creates a new instance with the required dependencies for JWT authentication.
     *
     * @param userDetailsService          Service that loads user-specific data
     * @param jwtAuthenticationEntryPoint Handler for authentication failures
     * @param jwtAuthenticationFilter     Filter that processes JWT tokens
     */
    @Autowired
    public SpringSecurityConfig(CustomUserDetailsService userDetailsService,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the security filter chain with JWT-based authentication.
     *
     * @param http The HttpSecurity to modify
     * @return The built SecurityFilterChain
     * @throws Exception If an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF as we use stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)

                // Configure request authorization
                .authorizeHttpRequests(auth -> auth
                        // API needs auth...
                        .requestMatchers("/api/users/**").authenticated()
                        // Except authentication...
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/**").permitAll())

                // Configure exception handling for unauthorized requests
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Use stateless sessions since we're using JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure the authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before the standard authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates an AuthenticationManager using the default configuration.
     *
     * @param authenticationConfiguration The Spring Security authentication
     *                                    configuration
     * @return The AuthenticationManager instance
     * @throws Exception If an error occurs creating the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Creates a password encoder for secure password handling.
     *
     * @return BCrypt password encoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication provider with our custom user details service
     * and password encoder.
     *
     * @return Configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
