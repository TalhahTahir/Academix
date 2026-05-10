package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.dto.VaultDTO;
import com.talha.academix.dto.VaultTransactionDTO;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.Role;
import com.talha.academix.enums.TxReferenceType;
import com.talha.academix.enums.TxStatus;
import com.talha.academix.enums.VaultTxType;
import com.talha.academix.exception.AlreadyExistException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.mapper.VaultMapper;
import com.talha.academix.model.Payment;
import com.talha.academix.model.Vault;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.VaultRepo;
import com.talha.academix.repository.VaultTransactionRepo;
import com.talha.academix.services.VaultService;
import com.talha.academix.services.VaultTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final VaultRepo vaultRepo;
    private final UserRepo userRepo;
    private final VaultTransactionRepo vaultTxRepo;
    private final VaultTransactionService vaultTxService;
    private final VaultMapper vaultMapper;

    @Override
    public VaultDTO createVault(VaultDTO dto) {
        Vault vault = vaultMapper.toEntity(dto);
        vault.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + dto.getUserId())));
        vault = vaultRepo.save(vault);
        return vaultMapper.toDto(vault);
    }

    @Override
    public VaultDTO updateVault(Long vaultId, VaultDTO dto) {
        Vault exist = vaultRepo.findById(vaultId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id : " + vaultId));
        vaultMapper.updateVaultfromDto(dto, exist);
        exist.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + dto.getUserId())));
        exist.setId(vaultId);
        exist = vaultRepo.save(exist);
        return vaultMapper.toDto(exist);
    }

    @Override
    public void deleteVault(Long vaultId) {

        Vault vault = vaultRepo.findById(vaultId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id : " + vaultId));
        vaultRepo.delete(vault);
    }

    @Override
    public VaultDTO getVaultById(Long vaultId) {
        Vault vault = vaultRepo.findById(vaultId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id : " + vaultId));
        return vaultMapper.toDto(vault);
    }

    @Override
    public VaultDTO getVaultByUserId(Long userId) {
        Vault vault = vaultRepo.findByUser_Userid(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found for user with id : " + userId));
        return vaultMapper.toDto(vault);
    }

    @Override
    public BigDecimal getTotalEarned() {

        return vaultRepo.getTotalEarned();
    }

    @Override
    public BigDecimal getTotalWithdrawn() {

        return vaultRepo.getTotalWithdrawn();
    }

    @Override
    public BigDecimal getTotalAvailableBalance() {

        return vaultRepo.getTotalAvailableBalance();
    }

    @Transactional
    @Override
    public Boolean shareDistribution(Payment payment) {

        if (vaultTxRepo.findByReferenceTypeAndReferenceId(TxReferenceType.PAYMENT, payment.getId()) != null) {
            throw new AlreadyExistException(
                    "Vault Transaction already exists for payment with id : " + payment.getId());

        }

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            Vault teacherVault = vaultRepo.findByUser_Userid(payment.getCourse().getTeacher().getUserid())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vault not found for teacher with id : " + payment.getCourse().getTeacher().getUserid()));

            Vault adminVault = vaultRepo.findByUser_Role(Role.ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Vault not found for admin with id : 1"));

            BigDecimal amount = payment.getAmount();

            VaultTransactionDTO teacherTx = new VaultTransactionDTO();
            teacherTx.setAmount(amount.multiply(BigDecimal.valueOf(0.80))); // 80
            teacherTx.setType(VaultTxType.CREDIT);
            teacherTx.setStatus(TxStatus.COMPLETED);
            teacherTx.setBalanceAfter(teacherVault.getAvailableBalance().add(teacherTx.getAmount()));
            teacherTx.setCreatedAt(Instant.now());
            teacherTx.setVaultId(teacherVault.getId());
            teacherTx.setReferenceType(TxReferenceType.PAYMENT);
            teacherTx.setReferenceId(payment.getId());
            teacherTx.setInitiaterId(payment.getUser().getUserid());

            vaultTxService.createTransaction(teacherTx);

            teacherVault.setAvailableBalance(teacherTx.getBalanceAfter());
            teacherVault.setTotalEarned(teacherVault.getTotalEarned().add(teacherTx.getAmount()));
            teacherVault.setUpdatedAt(Instant.now());
            vaultRepo.save(teacherVault);

            // ------------------------------------------------------------------------------

            VaultTransactionDTO adminTx = new VaultTransactionDTO();
            adminTx.setAmount(amount.multiply(BigDecimal.valueOf(0.20))); // 20
            adminTx.setType(VaultTxType.CREDIT);
            adminTx.setStatus(TxStatus.COMPLETED);
            adminTx.setBalanceAfter(adminVault.getAvailableBalance().add(adminTx.getAmount()));
            adminTx.setCreatedAt(Instant.now());
            adminTx.setVaultId(adminVault.getId());
            adminTx.setReferenceType(TxReferenceType.PAYMENT);
            adminTx.setReferenceId(payment.getId());
            adminTx.setInitiaterId(payment.getUser().getUserid());

            vaultTxService.createTransaction(adminTx);

            adminVault.setAvailableBalance(adminTx.getBalanceAfter());
            adminVault.setTotalEarned(adminVault.getTotalEarned().add(adminTx.getAmount()));
            adminVault.setUpdatedAt(Instant.now());

            vaultRepo.save(adminVault);

            return true;

        }
        return false;
    }

}
