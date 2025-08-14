package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.VaultTransactionDTO;

public interface VaultTransaction {
    
    VaultTransactionDTO createTransaction(VaultTransactionDTO dto);
    VaultTransactionDTO getTransactionById(Long transactionId);
    
    Long getTotalTransactionsByVaultId(Long vaultId);
    Long getTotalTransactionsByUserId(Long userId);
    Long getTotalTransactionsAmountByVaultId(Long vaultId);

    List<VaultTransactionDTO> getTransactionsByVaultId(Long vaultId);
    List<VaultTransactionDTO> getAllTransactions();
    List<VaultTransactionDTO> getTransactionsByCourse(Long userId);
    List<VaultTransactionDTO> getTransactionsByDay(String day);
    List<VaultTransactionDTO> getTransactionsByWeek(String week);
    List<VaultTransactionDTO> getTransactionsByMonth(String month);
    List<VaultTransactionDTO> getTransactionsByYear(String year);
}
