package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private Long walletID;
    private Long userID;
    private String jazzcash;
    private String easypaisa;
    private String bankAccount;
}
