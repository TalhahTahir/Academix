package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.talha.academix.enums.PaymentMedium;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private Long walletID;
    private Long userID;
    private PaymentMedium medium;
    private String account;
}
