package fr.cytech.projetdevwebbackend.users;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.cytech.projetdevwebbackend.users.service.UserDetailsProvider;

import java.io.IOException;

/**
 * JWT authentication filter that validates tokens and authenticates users.
 * <p>
 * This filter intercepts incoming requests, extracts and validates JWT tokens,
 * and sets up Spring Security authentication if a valid token is found.
 * It executes before the standard Spring Security filters.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsProvider userDetailsProvider;

    /**
     * Creates a new JWT authentication filter.
     *
     * @param jwtTokenProvider    Provider for JWT token operations
     * @param userDetailsProvider Provider to load user details
     */
    @Autowired
    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            @Qualifier("customUserDetailsService") UserDetailsProvider userDetailsProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsProvider = userDetailsProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Get JWT token from HTTP request
            String token = getTokenFromRequest(request);

            // Validate Token and authenticate user
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                // Get username from token
                String username = jwtTokenProvider.extractUsername(token);

                // Load user details using functional approach
                userDetailsProvider.findUserDetails(username).ifRight(userDetails -> {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    // Set details and context
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    logger.debug("User '{}' authenticated successfully", username);
                }).ifLeft(error -> logger.warn("Authentication failed: {}", error.getMessage()));
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the request's Authorization header.
     *
     * @param request The HTTP request
     * @return The JWT token string or null if not found
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
