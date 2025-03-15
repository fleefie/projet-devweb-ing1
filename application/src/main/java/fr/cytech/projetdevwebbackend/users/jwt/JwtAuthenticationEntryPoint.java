package fr.cytech.projetdevwebbackend.users.jwt;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Entry point for handling authentication failures in the security filter
 * chain.
 * <p>
 * This component is triggered when an unauthenticated client attempts to access
 * a protected resource. It returns an appropriate HTTP 401 Unauthorized
 * response
 * with a JSON payload containing error details.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * Called when an unauthenticated user attempts to access a secured endpoint.
     * Responds with a 401 Unauthorized status and a JSON error body.
     *
     * @param request       The request that resulted in an AuthenticationException
     * @param response      The response to modify
     * @param authException The exception that triggered this entry point
     * @throws IOException      If an input or output exception occurs
     * @throws ServletException If a servlet exception occurs
     */
    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        logger.error("Unauthorized access attempt: {}", authException.getMessage());

        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Create error response
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", new Date());
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", authException.getMessage());
        errorDetails.put("path", request.getRequestURI());

        // Write JSON response
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorDetails);
    }
}
