package fr.cytech.projetdevwebackend;

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

@Component
public class NotFoundIndexFilter extends OncePerRequestFilter {
    private final String contextPath;

    public NotFoundIndexFilter(@Value("${server.servlet.context-path:}") String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isHtmlRequest(request) && !isAPI(request)) {
            HttpServletRequest mutatedRequest = mutateRequestToIndexPage(request);
            filterChain.doFilter(mutatedRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private HttpServletRequestWrapper mutateRequestToIndexPage(HttpServletRequest request) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return contextPath + "/index.html";
            }
        };
    }

    private boolean isHtmlRequest(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains(MediaType.TEXT_HTML_VALUE);
    }

    private boolean isAPI(HttpServletRequest request) {
        return request.getRequestURI().startsWith(contextPath + "/api");
    }
}
