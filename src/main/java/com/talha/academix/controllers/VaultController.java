package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.VaultDTO;
import com.talha.academix.services.VaultService;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/vaults")
@RequiredArgsConstructor
public class VaultController {

    private final VaultService vaultService;

    @PostMapping("/{id}")
    public VaultDTO createVault(@PathVariable("id") Long userid) {
        VaultDTO dto = new VaultDTO();
        dto.setUserId(userid);
        dto.setAvailableBalance(BigDecimal.ZERO);
        dto.setTotalEarned(BigDecimal.ZERO);
        dto.setTotalWithdrawn(BigDecimal.ZERO);
        dto.setCurrency("USD");
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());

        return vaultService.createVault(dto);
    }
}
