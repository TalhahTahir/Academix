package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.VaultTransactionDTO;
import com.talha.academix.exception.PaymentFailedException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Payment;
import com.talha.academix.model.User;
import com.talha.academix.model.Vault;
import com.talha.academix.model.VaultTransaction;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.VaultRepo;
import com.talha.academix.repository.VaultTransactionRepo;
import com.talha.academix.services.VaultTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaultTransactionServiceImpl implements VaultTransactionService {

    private final ModelMapper mapper;
    private final VaultTransactionRepo vaultTxRepo;
    private final VaultRepo vaultRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    

    @Override
    public VaultTransactionDTO createTransaction(VaultTransactionDTO dto) {
        if (dto.getPaymentId() == null)
            throw new PaymentFailedException("Can't relate to any payment record");

        Vault vault = vaultRepo.findById(dto.getVaultId())
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id: " + dto.getVaultId()));

        Enrollment enrollment = enrollmentRepo.findById(dto.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + dto.getEnrollmentId()));

        Payment payment = paymentRepo.findById(dto.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + dto.getPaymentId()));

        User initiater = userRepo.findById(dto.getInitiaterId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getInitiaterId()));
                
        VaultTransaction vaultTx = new VaultTransaction();
        vaultTx.setAmount(dto.getAmount());
        vaultTx.setType(dto.getType());
        vaultTx.setStatus(dto.getStatus());
        vaultTx.setBalanceAfter(dto.getBalanceAfter());
        vaultTx.setCreatedAt(Instant.now());
        vaultTx.setVault(vault);
        vaultTx.setPayment(payment);
        vaultTx.setEnrollment(enrollment);
        vaultTx.setInitiater(initiater);

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
    public List<VaultTransactionDTO> getTransactionsByVaultId(Long vaultId) {   
        List<VaultTransaction> txs = vaultTxRepo.findAllByVaultId(vaultId);
        return txs.stream()
            .map(tx -> mapper.map(tx, VaultTransactionDTO.class))
            .toList();
    }

    @Override
    public List<VaultTransactionDTO> getAllTransactions() {
        List<VaultTransaction> vaultTxs = vaultTxRepo.findAll();

        return vaultTxs.stream()
                .map(tx -> mapper.map(tx, VaultTransactionDTO.class))
                .toList();
    }

    @Override
    public List<VaultTransactionDTO> getTransactionsByCourseId(Long courseId) {
        List<VaultTransaction> txs = vaultTxRepo.findAllByEnrollment_Course_Courseid(courseId);
        return txs.stream()
                .map(tx -> mapper.map(tx, VaultTransactionDTO.class))
                .toList();
    }

    @Override
public long countTransactionsByVaultId(Long vaultId) {
    return vaultTxRepo.countByVaultId(vaultId);
}

@Override
public BigDecimal getTotalTransactionAmountByVaultId(Long vaultId) {
    return vaultTxRepo.sumAmountByVaultId(vaultId);
}

@Override
public Page<VaultTransactionDTO> listTransactionsByVaultId(Long vaultId, Pageable p) {
    return vaultTxRepo.findAllByVaultId(vaultId, p)
            .map(tx -> mapper.map(tx, VaultTransactionDTO.class));
}

@Override
public Page<VaultTransactionDTO> listAllTransactions(Pageable p) {
    return vaultTxRepo.findAll(p)
            .map(tx -> mapper.map(tx, VaultTransactionDTO.class));
}

@Override
public List<VaultTransactionDTO> getTransactionsForDay(LocalDate date) {
    Instant start = date.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant end = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    return vaultTxRepo.findAllByCreatedAtBetween(start, end)
            .stream().map(tx -> mapper.map(tx, VaultTransactionDTO.class))
            .toList();
}

@Override
public List<VaultTransactionDTO> getTransactionsForWeek(LocalDate weekStart) {
    Instant start = weekStart.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant end = weekStart.plusWeeks(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    return vaultTxRepo.findAllByCreatedAtBetween(start, end)
            .stream().map(tx -> mapper.map(tx, VaultTransactionDTO.class))
            .toList();
}

@Override
public List<VaultTransactionDTO> getTransactionsForMonth(YearMonth month) {
    Instant start = month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant end = month.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();

    return vaultTxRepo.findAllByCreatedAtBetween(start, end)
            .stream().map(tx -> mapper.map(tx, VaultTransactionDTO.class))
            .toList();
}

@Override
public List<VaultTransactionDTO> getTransactionsForYear(int year) {
    Instant start = LocalDate.of(year, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant end = LocalDate.of(year + 1, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();

    return vaultTxRepo.findAllByCreatedAtBetween(start, end)
            .stream().map(tx -> mapper.map(tx, VaultTransactionDTO.class))
            .toList();
}

}
