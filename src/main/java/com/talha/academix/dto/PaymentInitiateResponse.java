package com.talha.academix.dto;

import com.talha.academix.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateResponse {
    private Long paymentId;
    private String clientSecret;
    private PaymentStatus status;
    private boolean requiresAction;
}