package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.WalletDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Wallet;
import com.talha.academix.repository.WalletRepo;
import com.talha.academix.services.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepo walletRepo;
    private final ModelMapper modelMapper;

    @Override
    public WalletDTO addWallet(WalletDTO dto) {
        Wallet wallet = modelMapper.map(dto, Wallet.class);
        wallet = walletRepo.save(wallet);
        return modelMapper.map(wallet, WalletDTO.class);
    }

    @Override
    public List<WalletDTO> getWalletsByUser(Long userId) {
        List<Wallet> wallets = walletRepo.findByUserID(userId);
        return wallets.stream()
                .map(w -> modelMapper.map(w, WalletDTO.class))
                .toList();
    }

    @Override
    public void deleteWallet(Long walletId) {
        Wallet wallet = walletRepo.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        walletRepo.delete(wallet);
    }
}
