package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.WithdrawalStatus;
import com.talha.academix.model.Withdrawal;

public interface WithdrawalRepo extends JpaRepository<Withdrawal, Long> {

   List<Withdrawal> findAllByRequestedBy_Id(Long userId);

   Optional<Withdrawal>findByProviderObjectId(String providerObjectId);

   boolean existsByVault_IdAndStatusIn(Long vaultId, List<WithdrawalStatus> statuses);
    
}
