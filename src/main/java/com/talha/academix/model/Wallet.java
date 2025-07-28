package com.talha.academix.model;

import java.time.Instant;

import com.talha.academix.enums.PaymentMedium;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable= false)
    private User user;

    @Enumerated(EnumType.STRING)
    private PaymentMedium medium; // Stripe, JazzCash, etc.

    private String account; // raw identifier (card, phone, IBAN, etc.)

    private String token; // gateway reusable token (payment_method_id, access_token, etc.)

    private String brand; // Visa, JazzCash, etc. (optional)

    private String accountReference; // masked value for UI display

    private Instant createdAt;

    private Instant updatedAt;
}