package com.talha.academix.model;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vault {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private Double balance; // Current balance in the vault
    private Double totalearned;
    private Double totalwithdrawn; // Total amount withdrawn from the vault
    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "vault", fetch = FetchType.LAZY)
    private List<Payment> payments;

    @OneToMany(mappedBy = "vault", fetch = FetchType.LAZY)
    private List<Wallet> withdrawals;

}
