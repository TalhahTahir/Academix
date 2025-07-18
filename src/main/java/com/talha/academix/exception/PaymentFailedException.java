package com.talha.academix.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }

    public PaymentFailedException(String message, String clientSecret) {
        super(message + " Client Secret: " + clientSecret);
    }
}
