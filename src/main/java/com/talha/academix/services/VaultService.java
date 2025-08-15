package com.talha.academix.services;

import java.math.BigDecimal;

import com.talha.academix.dto.VaultDTO;

public interface VaultService {
    VaultDTO createVault(VaultDTO dto);
    VaultDTO getVaultById(Long vaultId);
    VaultDTO updateVault(Long vaultId, VaultDTO dto);
    void deleteVault(Long vaultId);
    VaultDTO getVaultByUserId(Long userId);

    BigDecimal getTotalEarned();

    BigDecimal getTotalWithdrawn();

    BigDecimal getTotalAvailableBalance();



}
