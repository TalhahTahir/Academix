package com.talha.academix.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.talha.academix.dto.VaultTransactionDTO;

public interface VaultTransactionService {

    VaultTransactionDTO createTransaction(VaultTransactionDTO dto);

    VaultTransactionDTO getTransactionById(Long transactionId);

    long countTransactionsByVaultId(Long vaultId);

    BigDecimal getTotalTransactionAmountByVaultId(Long vaultId);

    List<VaultTransactionDTO> getTransactionsByVaultId(Long vaultId); // small sets only

    Page<VaultTransactionDTO> listTransactionsByVaultId(Long vaultId, Pageable p); // recommended for production

    List<VaultTransactionDTO> getAllTransactions();

    Page<VaultTransactionDTO> listAllTransactions(Pageable p); // recommended

    List<VaultTransactionDTO> getTransactionsByCourseId(Long courseId);

    List<VaultTransactionDTO> getTransactionsForDay(LocalDate date);

    List<VaultTransactionDTO> getTransactionsForWeek(LocalDate weekStart); // ISO week start

    List<VaultTransactionDTO> getTransactionsForMonth(YearMonth month);

    List<VaultTransactionDTO> getTransactionsForYear(int year);

}
