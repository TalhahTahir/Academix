package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.VaultTransaction;

public interface VaultTransactionRepo extends JpaRepository<VaultTransaction, Long> {
    
}
