package fr.cytech.projetdevwebbackend.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API errors.
 * Provides consistent error responses for various exception types.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors (@Valid annotation failures)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "The request contains invalid data",
                "VALIDATION_ERROR",
                request.getRequestURI());
        response.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles malformed JSON request bodies
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMalformedJsonRequest(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed Request",
                "The request body contains invalid JSON",
                "INVALID_JSON",
                request.getRequestURI()));
    }

    /**
     * Handles type mismatch errors in request parameters
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Type Mismatch",
                "Parameter '" + ex.getName() + "' has invalid value: " + ex.getValue(),
                "TYPE_MISMATCH",
                request.getRequestURI()));
    }

    /**
     * Creates a standardized error response structure
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String error,
            String message, String errorCode, String path) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        if (path != null) {
            response.put("path", path);
        }

        return response;
    }
}
