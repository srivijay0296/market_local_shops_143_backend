package com.marketlocalshops.exception;

import com.marketlocalshops.common.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Not Found")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .path(request != null ? request.getRequestURI() : null)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
            
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Forbidden")
                .message("Access denied: " + ex.getMessage())
                .status(HttpStatus.FORBIDDEN.value())
                .path(request != null ? request.getRequestURI() : null)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
            
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Unauthorized")
                .message("Authentication failed: Invalid username or password.")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request != null ? request.getRequestURI() : null)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
            
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            validationErrors.put(error.getField(), error.getDefaultMessage()));
            
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Bad Request")
                .message("Validation failed for one or more fields.")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request != null ? request.getRequestURI() : null)
                .validationErrors(validationErrors)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            RuntimeException ex, HttpServletRequest request) {
            
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Bad Request")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request != null ? request.getRequestURI() : null)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest request) {
            
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Database Integrity Violation")
                .message("Operation conflicts with existing data constraints.")
                .status(HttpStatus.CONFLICT.value())
                .path(request != null ? request.getRequestURI() : null)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {
            
        ApiErrorResponse response = ApiErrorResponse.builder()
                .error("Internal Server Error")
                .message(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request != null ? request.getRequestURI() : null)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
