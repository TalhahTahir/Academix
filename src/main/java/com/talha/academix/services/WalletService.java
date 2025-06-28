package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.WalletDTO;

public interface WalletService {
    WalletDTO addWallet(WalletDTO dto);

    List<WalletDTO> getWalletsByUser(Long userId);
    
    void deleteWallet(Long walletId);
}
