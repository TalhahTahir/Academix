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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="User_id", nullable= false)
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Course_id", nullable= false)
    private Course course;

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
