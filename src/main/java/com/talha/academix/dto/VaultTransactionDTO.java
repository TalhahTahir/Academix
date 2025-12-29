package com.talha.academix.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.TxReferenceType;
import com.talha.academix.enums.TxStatus;
import com.talha.academix.enums.VaultTxType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaultTransactionDTO {
        private Long id;
    private Long vaultId;
    private BigDecimal amount;
    private VaultTxType type;           // ENROLLMENT_CREDIT | WITHDRAWAL_REQUEST | WITHDRAWAL_PAYOUT
    private TxStatus status;            //     PENDING | COMPLETED | FAILED
    private Long initiaterId;
    private TxReferenceType referenceType;
    private Long referenceId;
    private BigDecimal balanceAfter;
    private Instant createdAt;
}
