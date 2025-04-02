package fr.cytech.projetdevwebbackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Request filter that handles routing for a React Single Page Application
 * (SPA).
 * <p>
 * This filter intercepts incoming HTTP requests and redirects non-API HTML
 * requests
 * to the application's index.html page, enabling the React router to handle
 * client-side routing.
 * This approach allows the SPA to properly handle deep-linking and browser
 * refreshes
 * on routes that aren't explicit server endpoints.
 */
@Component
public class NotFoundIndexFilter extends OncePerRequestFilter {
    private final String contextPath;

    /**
     * Creates a new instance of NotFoundIndexFilter.
     *
     * @param contextPath The application's context path, injected from application
     *                    properties
     *                    (defaults to empty string if not specified)
     */
    public NotFoundIndexFilter(@Value("${server.servlet.context-path:}") String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * Processes each request to determine if it should be redirected to the index
     * page.
     * <p>
     * Non-API requests that accept HTML responses are redirected to the SPA's
     * index.html,
     * while API requests and non-HTML requests pass through unchanged.
     *
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @param filterChain The filter chain for request processing
     * @throws ServletException If an exception occurs during request processing
     * @throws IOException      If an I/O exception occurs during request processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isHtmlRequest(request) && !isApiRequest(request)) {
            HttpServletRequest mutatedRequest = mutateRequestToIndexPage(request);
            filterChain.doFilter(mutatedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Creates a wrapped request that modifies the request URI to point to
     * index.html.
     *
     * @param request The original HTTP request
     * @return A wrapped request with the URI changed to index.html
     */
    private HttpServletRequestWrapper mutateRequestToIndexPage(HttpServletRequest request) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return contextPath + "/index.html";
            }
        };
    }

    /**
     * Determines if the request is for HTML content based on the Accept header.
     *
     * @param request The HTTP request
     * @return true if the request accepts HTML content, false otherwise
     */
    private boolean isHtmlRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains(MediaType.TEXT_HTML_VALUE);
    }

    /**
     * Determines if the request is targeting the API.
     *
     * @param request The HTTP request
     * @return true if the request URI starts with the API path, false otherwise
     */
    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith(contextPath + "/api")
                || request.getRequestURI().startsWith(contextPath + "/v3/api-docs")
                || request.getRequestURI().startsWith(contextPath + "/swagger-ui");
    }
}
