package com.talha.academix.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String clientSecret;
    private String statusMessage;
    private String paymentIntentId; // Stripe's PaymentIntent ID
}
