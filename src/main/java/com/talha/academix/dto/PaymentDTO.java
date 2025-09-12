package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.PaymentProvider;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.PaymentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Long id;
    private Long userId;
    private Long courseId;
    private BigDecimal amount;
    private String currency;
    private PaymentType paymentType;
    private PaymentProvider provider;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant succeededAt;
}