package com.talha.academix.model;


import java.math.BigDecimal;
import java.time.Instant;

import com.talha.academix.enums.PaymentProvider;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.PaymentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments",
       indexes = {
           @Index(name="idx_payment_user", columnList = "user_id"),
           @Index(name="idx_payment_course", columnList = "course_id"),
           @Index(name="idx_payment_status", columnList = "status"),
           @Index(name="idx_payment_created_at", columnList = "createdAt")
       })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="course_id", nullable=false)
    private Course course;

    @Column(nullable=false, precision=18, scale=2)
    private BigDecimal amount;

    @Column(length=3, nullable=false)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(length=20, nullable=false)
    private PaymentType paymentType = PaymentType.INCOMING;

    @Enumerated(EnumType.STRING)
    @Column(length=20, nullable=false)
    private PaymentProvider provider = PaymentProvider.STRIPE;

    @Enumerated(EnumType.STRING)
    @Column(length=25, nullable=false)
    private PaymentStatus status = PaymentStatus.CREATED;

    @Column(nullable=false, updatable=false)
    private Instant createdAt;

    @Column(nullable=false)
    private Instant updatedAt;

    private Instant succeededAt;
    private Instant failedAt;
    private Instant canceledAt;
    private Instant refundedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}