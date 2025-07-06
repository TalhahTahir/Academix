package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.WalletDTO;

public interface WalletService {
    WalletDTO addOrUpdateWallet(WalletDTO dto);
    WalletDTO getWalletById(Long walletId);
    List<WalletDTO> getWalletsByUser(Long userId);
    void deleteWallet(Long walletId);
}