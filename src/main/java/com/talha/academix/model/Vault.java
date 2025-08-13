package com.talha.academix.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="vault")
public class Vault {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, unique=true)
    private User user;

    @Column(name="available_balance", nullable=false, precision=18, scale=2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name="total_earned", nullable=false, precision=18, scale=2)
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(name="total_withdrawn", nullable=false, precision=18, scale=2)
    private BigDecimal totalWithdrawn = BigDecimal.ZERO;

    @Column(name="currency", length=3, nullable=false)
    private String currency = "USD";

    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

}
