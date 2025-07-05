package com.talha.academix.repository;

import com.talha.academix.model.Wallet;
import com.talha.academix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepo extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}
