package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.VaultTransaction;

public interface VaultTransactionRepo extends JpaRepository<VaultTransaction, Long> {

    List<VaultTransaction> findAllByCourseId(Long courseId);

    List<VaultTransaction> findAllByVaultId(Long vaultId);
    
}
