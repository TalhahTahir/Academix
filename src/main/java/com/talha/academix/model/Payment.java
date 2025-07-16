package com.talha.academix.model;

import java.util.Date;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Course_id", nullable = false)
    private Course course;

    @NotNull
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentMedium medium;

    @NotEmpty
    private String account;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String gatewayTransactionId; // Stripe's PaymentIntent ID
    private String gatewayStatus; // 'succeeded', 'requires_action', etc.

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

}
