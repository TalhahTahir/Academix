package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.PaymentProvider;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.PaymentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Long userId;
    @NotNull
    private Long courseId;
    @NotNull
    private BigDecimal amount;
    @NotBlank
    private String currency;
    @NotNull
    private PaymentType paymentType;
    @NotNull
    private PaymentProvider provider;
    @NotNull
    private PaymentStatus status;
    @NotNull
    private Instant createdAt;
    private Instant updatedAt;
    private Instant succeededAt;
}