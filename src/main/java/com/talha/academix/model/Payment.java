package com.talha.academix.model;

import java.util.Date;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentID;

    @NotNull
    private Long userID;

    @NotNull
    private Long courseID;

    @NotNull
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentMedium medium;

    @NotEmpty
    private String account;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
}
