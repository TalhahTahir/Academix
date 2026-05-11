package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaultDTO {
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private BigDecimal availableBalance;
    @NotNull
    private BigDecimal totalEarned;
    @NotNull
    private BigDecimal totalWithdrawn;
    @NotNull
    private BigDecimal pendingWithdrawal;
    @NotBlank
    private String currency;
    @NotNull
    private Instant createdAt;
    private Instant updatedAt;

}
