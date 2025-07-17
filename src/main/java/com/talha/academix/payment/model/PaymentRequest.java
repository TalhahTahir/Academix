package com.talha.academix.payment.model;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long amount;
    private String account;
    private PaymentType type;
    private PaymentMedium medium;
}

