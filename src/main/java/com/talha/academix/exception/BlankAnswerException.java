package com.talha.academix.exception;

public class BlankAnswerException extends RuntimeException {

    public BlankAnswerException(String message) {
        super(message);
    }

    public BlankAnswerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
