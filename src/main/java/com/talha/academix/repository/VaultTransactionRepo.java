package com.talha.academix.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.VaultTransaction;

public interface VaultTransactionRepo extends JpaRepository<VaultTransaction, Long> {

    List<VaultTransaction> findAllByCourseId(Long courseId);

    List<VaultTransaction> findAllByVaultId(Long vaultId);

    long countByVaultId(Long vaultId);

    @Query("SELECT COALESCE(SUM(vt.amount), 0) FROM VaultTransaction vt WHERE vt.vault.id = :vaultId")
    BigDecimal sumAmountByVaultId(@Param("vaultId") Long vaultId);

    Page<VaultTransaction> findAllByVaultId(Long vaultId, Pageable pageable);

    @Query("SELECT vt FROM VaultTransaction vt WHERE vt.createdAt BETWEEN :start AND :end")
    List<VaultTransaction> findAllByCreatedAtBetween(@Param("start") Instant start, @Param("end") Instant end);
    
}
