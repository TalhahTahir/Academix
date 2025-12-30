package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.WithdrawalKind;
import com.talha.academix.enums.WithdrawalStatus;

import lombok.Data;

@Data
public class WithdrawalDTO {
    private Long id;
    private Long vaultId;
    private Long requestedById;
    private WithdrawalKind kind;
    private BigDecimal amount;
    private WithdrawalStatus status;
    private String providerObjectId;
    private Instant requestedAt;
    private Instant processedAt;
}