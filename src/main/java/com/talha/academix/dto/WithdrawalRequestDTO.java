package com.talha.academix.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawalRequestDTO {

    @NotNull
    private Long userId;

    @NotNull
    @DecimalMin(value = "10.00", message = "Minimum withdrawal is $10")
    @Digits(integer = 16, fraction = 2)
    private BigDecimal amount;
}