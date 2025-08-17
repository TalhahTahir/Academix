package com.talha.academix.services.impl;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.VaultDTO;
import com.talha.academix.exception.AlreadyExistException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Vault;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.VaultRepo;
import com.talha.academix.services.VaultService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final VaultRepo vaultRepo;
    private final ModelMapper mapper;
    private final UserRepo userRepo;

    @Override
    public VaultDTO createVault(VaultDTO dto) {
        VaultDTO exist = getVaultByUserId(dto.getUserId());
        if (exist != null) {
            throw new AlreadyExistException("Vault already exists for user with ID: " + dto.getUserId());
        }
        Vault vault = mapper.map(dto, Vault.class);
        vault.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + dto.getUserId())));
        vault = vaultRepo.save(vault);
        return mapper.map(vault, VaultDTO.class);
    }

    @Override
    public VaultDTO getVaultById(Long vaultId) {
        Vault vault = vaultRepo.findById(vaultId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id : " + vaultId));
        return mapper.map(vault, VaultDTO.class);
    }

    @Override
    public VaultDTO updateVault(Long vaultId, VaultDTO dto) {
        Vault exist = vaultRepo.findById(vaultId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id : " + vaultId));
        mapper.map(dto, exist);
        exist.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + dto.getUserId())));
        exist = vaultRepo.save(exist);
        return mapper.map(exist, VaultDTO.class);
    }

    @Override
    public void deleteVault(Long vaultId) {

        Vault vault = vaultRepo.findById(vaultId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id : " + vaultId));
        vaultRepo.delete(vault);
    }

    @Override
    public VaultDTO getVaultByUserId(Long userId) {
        Vault vault = vaultRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found for user with id : " + userId));
        return mapper.map(vault, VaultDTO.class);
    }

    @Override
    public BigDecimal getTotalEarned() {

        return vaultRepo.getTotalEarned();
    }

    @Override
    public BigDecimal getTotalWithdrawn() {

        return vaultRepo.getTotalWithdrawn();
    }

    @Override
    public BigDecimal getTotalAvailableBalance() {

        return vaultRepo.getTotalAvailableBalance();
    }

}
