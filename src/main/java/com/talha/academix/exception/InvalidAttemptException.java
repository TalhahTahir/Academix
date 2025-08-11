package com.talha.academix.exception;

public class InvalidAttemptException extends RuntimeException {

    public InvalidAttemptException(String message) {
        super(message);
    }

    public InvalidAttemptException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
