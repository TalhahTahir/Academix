package com.talha.academix.model;

import com.talha.academix.enums.PaymentMedium;


import jakarta.persistence.*;
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
    private PaymentMedium medium;
    
    private String account;
}
