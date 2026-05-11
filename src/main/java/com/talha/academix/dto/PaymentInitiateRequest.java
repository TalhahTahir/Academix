package com.talha.academix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentInitiateRequest {

    @NotNull
    private Long userId;
    @NotNull
    private Long courseId;
    // future: couponCode, walletUsage, etc.
}