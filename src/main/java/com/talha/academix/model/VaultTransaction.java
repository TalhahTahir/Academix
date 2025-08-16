package com.talha.academix.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.TxStatus;
import com.talha.academix.enums.VaultTxType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="vault_transaction")
public class VaultTransaction {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vault_id", nullable=false)
    private Vault vault;

    @Column(nullable=false, precision=18, scale=2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private VaultTxType type; // ENROLLMENT_CREDIT | WITHDRAWAL_REQUEST | WITHDRAWAL_PAYOUT

    @Enumerated(EnumType.STRING)
    private TxStatus status; // PENDING | COMPLETED | FAILED

    private Payment payment;
    private Enrollment enrollment;
    private User initiater;

    @Column(precision=18, scale=2)
    private BigDecimal balanceAfter;

    private Instant createdAt;
}

