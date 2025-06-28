package com.talha.academix.controllers;

import com.talha.academix.dto.WalletDTO;
import com.talha.academix.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;


    @PostMapping
    public WalletDTO addOrUpdateWallet(@RequestBody WalletDTO dto) {
        return walletService.addWallet(dto);
    }
    
    @GetMapping("/user/{userId}")
    public List<WalletDTO> getWalletsByUser(@PathVariable Long userId) {
        return walletService.getWalletsByUser(userId);
    }

    @DeleteMapping("/{walletId}")
    public void deleteWallet(@PathVariable Long walletId) {
        walletService.deleteWallet(walletId);
    }
}
