package com.talha.academix.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String clientSecret;
    private String statusMessage;
    private String transactionId;      
    private boolean requiresAction;    
}
