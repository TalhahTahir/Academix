package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Vault;

public interface  VaultRepo extends JpaRepository<Vault, Long> {
    
}
