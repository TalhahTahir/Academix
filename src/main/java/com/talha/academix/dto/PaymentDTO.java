package com.talha.academix.dto;

import java.util.Date;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentID;
    private Long userID;
    private Long courseID;
    private Integer amount;
    private PaymentMedium medium;
    private PaymentType paymentType;
    private String account;
    private String gatewayTransactionId; // Stripe's PaymentIntent ID
    private String gatewayStatus;        // 'succeeded', 'requires_action', etc.    
    private Date date;
}
