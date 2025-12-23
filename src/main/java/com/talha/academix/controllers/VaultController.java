package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.VaultDTO;
import com.talha.academix.services.VaultService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/vaults")
@RequiredArgsConstructor
public class VaultController {

    private final VaultService vaultService;

    @GetMapping("/users/{id}")
    public VaultDTO getvaultByUserId(@PathVariable("id") Long userid) {
        return vaultService.getVaultByUserId(userid);
    }

    @GetMapping("/{id}")
    public VaultDTO getVaultById(@PathVariable Long id) {
        return vaultService.getVaultById(id);
    }

    @PutMapping("update/{id}")
    public VaultDTO updateVault(@PathVariable Long id, @RequestBody VaultDTO dto) {
        return vaultService.updateVault(Long.valueOf(id), dto);
    }

    @DeleteMapping("{id}")
    public void deleteVault(@PathVariable Long id) {
        vaultService.deleteVault(id);
    }

}
