package com.talha.academix.config;

import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.talha.academix.enums.Role;
import com.talha.academix.model.User;
import com.talha.academix.model.Vault;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.VaultRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final VaultRepo vaultRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-email}")
    private String email;

    @Value("${admin.default-password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {

        if (!userRepo.existsByRole(Role.ADMIN)) {
            log.info("Admin not found. seeding a default one....");

            User admin = new User();
            admin.setUsername("Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setGender("Not Specified");
            admin.setPhone("0000000000");
            admin.setRole(Role.ADMIN);
            admin.setCreatedAt(Instant.now());

            userRepo.save(admin);

            Vault adminVault = new Vault();
            adminVault.setUser(admin);
            adminVault.setAvailableBalance(BigDecimal.ZERO);
            adminVault.setTotalEarned(BigDecimal.ZERO);
            adminVault.setTotalWithdrawn(BigDecimal.ZERO);
            adminVault.setPendingWithdrawal(BigDecimal.ZERO);
            adminVault.setCurrency("USD");
            adminVault.setCreatedAt(Instant.now());
            adminVault.setUpdatedAt(Instant.now());

            vaultRepo.save(adminVault);

            log.info("Default admin created with email: {}", email);
        } else {
            log.info("Admin user already exists. Skipping admin seeding.");
        }
    }
}
