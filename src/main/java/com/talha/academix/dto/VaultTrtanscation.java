package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.TxStatus;
import com.talha.academix.enums.VaultTxType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaultTrtanscation {
        private Long id;
    private Long vaultId;
    private BigDecimal amount;
    private VaultTxType type;
    private TxStatus status;
    private Long relatedPaymentId;
    private Long relatedEnrollmentId;
    private Long initiatedBy;
    private BigDecimal balanceAfter;
    private String notes;
    private Instant createdAt;
}
