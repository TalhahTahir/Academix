package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.TxReferenceType;
import com.talha.academix.enums.TxStatus;
import com.talha.academix.enums.VaultTxType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaultTransactionDTO {
    
    private Long id;
    @NotNull
    private Long vaultId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private VaultTxType type;           // ENROLLMENT_CREDIT | WITHDRAWAL_REQUEST | WITHDRAWAL_PAYOUT
    @NotNull
    private TxStatus status;            //     PENDING | COMPLETED | FAILED
    @NotNull
    private Long initiaterId;
    @NotNull
    private TxReferenceType referenceType;
    @NotNull
    private Long referenceId;
    @NotNull
    private BigDecimal balanceAfter;
    @NotNull
    private Instant createdAt;
}
