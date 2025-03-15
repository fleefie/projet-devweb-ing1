package fr.cytech.projetdevwebbackend.errors;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures custom error pages to prevent redirects to /error
     */
    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return registry -> {
            // Add custom error pages for 4xx and 5xx status codes
            registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/api/error/400"));
            registry.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/api/error/401"));
            registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/api/error/403"));
            registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/api/error/404"));
            registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/api/error/500"));
        };
    }
}
