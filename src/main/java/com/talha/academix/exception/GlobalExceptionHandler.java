package com.talha.academix.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StripeOnboardingRequiredException.class)
    public ResponseEntity<Map<String, Object>> handleStripeOnboardingRequired(StripeOnboardingRequiredException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "message", ex.getMessage(),
                "onboardingUrl", ex.getOnboardingUrl()
        ));
    }
}