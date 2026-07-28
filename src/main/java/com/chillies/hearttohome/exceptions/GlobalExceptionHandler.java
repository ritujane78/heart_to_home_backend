package com.chillies.hearttohome.exceptions;

import com.stripe.exception.StripeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {

        String message = "Unable to save the service.";

        String error = ex.getMostSpecificCause().getMessage();

        if (error != null && error.contains("Duplicate entry")) {
            message = "A service with this code already exists. Please use a different code.";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<Map<String, String>> handleStripeException(
            StripeException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message",
                        "Unable to process your payment. " +
                                ex.getMessage()
                ));
    }
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Map<String, String>> handleEmailSendingException(
            EmailSendingException ex) {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message",
                        ex.getMessage()
                ));
    }
}