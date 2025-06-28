package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.talha.academix.model.Wallet;

public interface WalletRepo extends JpaRepository<Wallet, Long> {
    List<Wallet> findByUserID(Long userId);

}
