package com.talha.academix.model;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.TxStatus;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="withdrawal")
public class Withdrawal {
    
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vault_id", nullable=false)
    private Vault vault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="requested_by", nullable=false)
    private User requestedBy;

    @Column(nullable=false, precision=18, scale=2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private TxStatus status; // PENDING | APPROVED | REJECTED | PAID_OUT

    @Column(nullable=false)
    private String providerObjectId; // e.g., (tr_... or po_...)

    private Instant requestedAt;

    private Instant processedAt;

}
