package com.talha.academix.dto;

import lombok.Data;

@Data
public class PaymentInitiateRequest {
    private Long userId;
    private Long courseId;
    // future: couponCode, walletUsage, etc.
}