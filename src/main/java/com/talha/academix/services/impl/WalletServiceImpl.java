// WalletServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.WalletDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.User;
import com.talha.academix.model.Wallet;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.WalletRepo;
import com.talha.academix.services.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepo walletRepo;
    private final UserRepo userRepo;
    private final ModelMapper mapper;

    @Override
    public WalletDTO addOrUpdateWallet(WalletDTO dto) {
        User user = userRepo.findById(dto.getUserID())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserID()));

        Wallet wallet = walletRepo.findByUser(user)
            .orElseGet(() -> {
                Wallet w = new Wallet();
                w.setUser(user);
                return w;
            });

        wallet.setMedium(dto.getMedium());
        wallet.setAccount(dto.getAccount());

        wallet = walletRepo.save(wallet);
        return mapper.map(wallet, WalletDTO.class);
    }

    @Override
    public WalletDTO getWalletById(Long walletId) {
        Wallet wallet = walletRepo.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));
        return mapper.map(wallet, WalletDTO.class);
    }

    @Override
    public List<WalletDTO> getWalletsByUser(Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return walletRepo.findByUser(user).stream()
            .map(w -> mapper.map(w, WalletDTO.class))
            .toList();
    }

    @Override
    public void deleteWallet(Long walletId) {
        Wallet wallet = walletRepo.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));
        walletRepo.delete(wallet);
    }
}
