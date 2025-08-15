package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.talha.academix.dto.VaultTransactionDTO;
import com.talha.academix.services.VaultTransactionService;

public class VaultTransactionServiceImpl implements  VaultTransactionService {

    @Override
    public VaultTransactionDTO createTransaction(VaultTransactionDTO dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTransaction'");
    }

    @Override
    public VaultTransactionDTO getTransactionById(Long transactionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionById'");
    }

    @Override
    public long countTransactionsByVaultId(Long vaultId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'countTransactionsByVaultId'");
    }

    @Override
    public BigDecimal getTotalTransactionAmountByVaultId(Long vaultId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalTransactionAmountByVaultId'");
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsByVaultId(Long vaultId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionsByVaultId'");
    }

    @Override
    public Page<VaultTransactionDTO> listTransactionsByVaultId(Long vaultId, Pageable p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listTransactionsByVaultId'");
    }

    @Override
    public List<VaultTransactionDTO> getAllTransactions() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllTransactions'");
    }

    @Override
    public Page<VaultTransactionDTO> listAllTransactions(Pageable p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listAllTransactions'");
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsByCourseId(Long courseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionsByCourseId'");
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsForDay(LocalDate date) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionsForDay'");
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsForWeek(LocalDate weekStart) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionsForWeek'");
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsForMonth(YearMonth month) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionsForMonth'");
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsForYear(int year) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTransactionsForYear'");
    }

 

    
}
