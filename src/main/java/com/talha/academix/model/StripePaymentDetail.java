package com.talha.academix.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stripe_payment_detail",
       indexes = {
           @Index(name="idx_stripe_intent", columnList = "payment_intent_id", unique = true),
           @Index(name="idx_stripe_latest_status", columnList = "latest_provider_status")
       })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StripePaymentDetail {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="payment_id", nullable=false, unique=true)
    private Payment payment;

    @Column(name="payment_intent_id", length=120, nullable=false, unique=true)
    private String paymentIntentId;

    @Column(name="latest_charge_id", length=120)
    private String latestChargeId;

    @Column(name="payment_method_id", length=120)
    private String paymentMethodId;

    @Column(name="latest_provider_status", length=50)
    private String latestProviderStatus;

    @Column(name="failure_code", length=100)
    private String failureCode;

    @Column(name="failure_message", length=500)
    private String failureMessage;

    @Column(name="card_brand", length=30)
    private String cardBrand;

    @Column(name="card_last4", length=4)
    private String cardLast4;

    @Column(name="amount_received", precision=18, scale=2)
    private BigDecimal amountReceived;

    @Column(name="requires_capture")
    private Boolean requiresCapture;

    @Lob
    @Column(name="raw_latest_intent", columnDefinition = "LONGTEXT")
    private String rawLatestIntent;

    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @Column(nullable=false)
    private Instant updatedAt;

    @PrePersist
    void prePersist(){
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate(){
        updatedAt = Instant.now();
    }
}