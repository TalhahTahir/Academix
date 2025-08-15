package com.talha.academix.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.talha.academix.model.Vault;

@Repository
public interface VaultRepo extends JpaRepository<Vault, Long> {

    Optional<Vault> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(v.totalEarned), 0) FROM Vault v")
    BigDecimal getTotalEarned();

    @Query("SELECT COALESCE(SUM(v.totalWithdrawn), 0) FROM Vault v")
    BigDecimal getTotalWithdrawn();

    @Query("SELECT COALESCE(SUM(v.availableBalance), 0) FROM Vault v")
    BigDecimal getTotalAvailableBalance();

}
