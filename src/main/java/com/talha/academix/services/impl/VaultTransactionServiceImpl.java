package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.VaultTransactionDTO;
import com.talha.academix.exception.PaymentFailedException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.VaultTransaction;
import com.talha.academix.repository.VaultTransactionRepo;
import com.talha.academix.services.VaultTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaultTransactionServiceImpl implements VaultTransactionService {

    private final ModelMapper mapper;
    private final VaultTransactionRepo vaultTxRepo;

    @Override
    public VaultTransactionDTO createTransaction(VaultTransactionDTO dto) {
        if (dto.getPaymentId() == null)
            throw new PaymentFailedException("Can't relate to any payment record");

        VaultTransaction vaultTx = mapper.map(dto, VaultTransaction.class);
        vaultTx = vaultTxRepo.save(vaultTx);
        return mapper.map(vaultTx, VaultTransactionDTO.class);
        /*
         * entity has object like enrollment. DTO has enrollmentId.
         * so when mapping a DTO to Entity object will that typeMisMatch create an error
         * if not then how this will be handled
         */

    }

    @Override
    public VaultTransactionDTO getTransactionById(Long transactionId) {
        VaultTransaction vaultTx = vaultTxRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vault Transaction record not found with id: " + transactionId));

        return mapper.map(vaultTx, VaultTransactionDTO.class);
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
        List<VaultTransaction> txs = vaultTxRepo.findAllByVaultId(vaultId);
        return txs.stream()
            .map(tx -> mapper.map(tx, VaultTransactionDTO.class))
            .toList();
    }

    @Override
    public Page<VaultTransactionDTO> listTransactionsByVaultId(Long vaultId, Pageable p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listTransactionsByVaultId'");
    }

    @Override
    public List<VaultTransactionDTO> getAllTransactions() {
        List<VaultTransaction> vaultTxs = vaultTxRepo.findAll();

        return vaultTxs.stream()
                .map(tx -> mapper.map(tx, VaultTransactionDTO.class))
                .toList();
    }

    @Override
    public Page<VaultTransactionDTO> listAllTransactions(Pageable p) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listAllTransactions'");
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsByCourseId(Long courseId) {

        List<VaultTransaction> txs = vaultTxRepo.findAllByCourseId(courseId);
        return txs.stream()
                .map(tx -> mapper.map(tx, VaultTransactionDTO.class))
                .toList();
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
