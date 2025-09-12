package com.talha.academix.dto;

import java.time.ZonedDateTime;

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
    private PaymentType paymentType;
    private String account;
    private String gatewayTransactionId;
    private String gatewayStatus;
    private ZonedDateTime date;
    private String clientSecret;
    private Boolean requiresAction;
}

