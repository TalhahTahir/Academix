package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaultDTO {
    private Long id;
    private Long userId;
    private BigDecimal availableBalance;
    private BigDecimal totalEarned;
    private BigDecimal totalWithdrawn;
    private String currency;
    private Instant createdAt;
    private Instant updatedAt;

}
