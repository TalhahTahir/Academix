package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.WithdrawalKind;
import com.talha.academix.enums.WithdrawalStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawalDTO {

    private Long id;
    @NotNull
    private Long vaultId;
    @NotNull
    private Long requestedById;
    @NotNull
    private WithdrawalKind kind;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private WithdrawalStatus status;
    @NotBlank
    private String providerObjectId;
    @NotNull
    private Instant requestedAt;
    private Instant processedAt;
}