package fr.cytech.projetdevwebbackend.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller to handle error pages with API-friendly responses
 */
@RestController
@RequestMapping("/api/error")
public class ErrorController {

    @GetMapping("/400")
    public ResponseEntity<Object> handleBadRequest(HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.BAD_REQUEST, "Bad Request");
    }

    @GetMapping("/401")
    public ResponseEntity<Object> handleUnauthorized(HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    @GetMapping("/403")
    public ResponseEntity<Object> handleForbidden(HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.FORBIDDEN, "Forbidden");
    }

    @GetMapping("/404")
    public ResponseEntity<Object> handleNotFound(HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.NOT_FOUND, "Not Found");
    }

    @GetMapping("/500")
    public ResponseEntity<Object> handleServerError(HttpServletRequest request) {
        return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ResponseEntity<Object> createErrorResponse(HttpServletRequest request, HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", status.getReasonPhrase());

        return new ResponseEntity<>(body, status);
    }
}
