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
    private String token;     // Used for tokenized payments (payment_method_id, payer_id, etc.)
    private Long walletId;    // Reference to user's wallet (for selecting existing method)
}

