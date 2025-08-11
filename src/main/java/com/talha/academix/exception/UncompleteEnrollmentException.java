package com.talha.academix.exception;

public class UncompleteEnrollmentException extends RuntimeException {

    public UncompleteEnrollmentException(String message) {
        super(message);
    }

    public UncompleteEnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
