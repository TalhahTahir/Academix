package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Vault;
import com.talha.academix.repository.VaultRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("vaultSecurity")
public class VaultSecurity {
    
    private final VaultRepo vaultRepo;

    public boolean isVaultOwner(CustomUserDetails principal, Long vaultId) {
        if (principal == null || vaultId == null) {
            return false;
        }

        Vault vault = vaultRepo.findById(vaultId).orElseThrow(() -> new ResourceNotFoundException("Vault not found with id: " + vaultId));
        return vault.getUser().getUserid().equals(principal.getId());
    }
}
