package com.example.ticket_system.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.warn("Authentication failed: {}", ex.getMessage());
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                "Invalid username or password.",
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Illegal argument or resource not found: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();

        if (message != null && message.toLowerCase().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        }

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                message,
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        logger.error("Illegal state encountered: {}", ex.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                ex.getMessage(),
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false).substring(4)
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
