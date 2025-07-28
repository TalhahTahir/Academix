package com.talha.academix.dto;

import java.time.Instant;

import com.talha.academix.enums.PaymentMedium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private Long walletID;
    private Long userID;
    private PaymentMedium medium;
    private String account;
    private String token;
    private String brand;
    private String accountReference;
    private Instant createdAt;
    private Instant updatedAt;
}
