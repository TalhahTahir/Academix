package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.VaultDTO;
import com.talha.academix.dto.VaultTransactionDTO;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.Role;
import com.talha.academix.enums.TxStatus;
import com.talha.academix.enums.VaultTxType;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Payment;
import com.talha.academix.model.Vault;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.VaultRepo;
import com.talha.academix.services.VaultService;
import com.talha.academix.services.VaultTransactionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VaultServiceImpl implements VaultService {

    private final VaultRepo vaultRepo;
    private final ModelMapper mapper;
    private final UserRepo userRepo;
    private final VaultTransactionService vaultTxService;

    @Override
    public VaultDTO createVault(VaultDTO dto) {
        // VaultDTO exist = getVaultByUserId(dto.getUserId());
        // if (exist != null) {
        //     throw new AlreadyExistException("Vault already exists for user with ID: " + dto.getUserId());
        // }
        Vault vault = mapper.map(dto, Vault.class);
        vault.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + dto.getUserId())));
        vault = vaultRepo.save(vault);
        return mapper.map(vault, VaultDTO.class);
    }

    @Override
    public VaultDTO updateVault(Long vaultId, VaultDTO dto) {
        Vault exist = vaultRepo.findById(vaultId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found with id : " + vaultId));
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, exist);
        exist.setUser(userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + dto.getUserId())));
        exist.setId(vaultId);
        exist = vaultRepo.save(exist);
        return mapper.map(exist, VaultDTO.class);
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
        return mapper.map(vault, VaultDTO.class);
    }

    @Override
    public VaultDTO getVaultByUserId(Long userId) {
        Vault vault = vaultRepo.findByUser_Userid(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vault not found for user with id : " + userId));
        return mapper.map(vault, VaultDTO.class);
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

    @Override
    public Boolean shareDistribution(Payment payment) {
        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            Vault teacherVault = vaultRepo.findByUser_Userid(payment.getCourse().getTeacher().getUserid())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vault not found for teacher with id : " + payment.getCourse().getTeacher().getUserid()));

            Vault adminVault = vaultRepo.findByUser_Role(Role.ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Vault not found for admin with id : 1"));

            BigDecimal amount = payment.getAmount();

            VaultTransactionDTO teacherTx = new VaultTransactionDTO();
            teacherTx.setAmount(amount.multiply(BigDecimal.valueOf(0.80))); // 80
            teacherTx.setType(VaultTxType.ENROLLMENT_CREDIT);
            teacherTx.setStatus(TxStatus.COMPLETED);
            teacherTx.setBalanceAfter(teacherVault.getAvailableBalance().add(teacherTx.getAmount()));
            teacherTx.setCreatedAt(Instant.now());
            teacherTx.setVaultId(teacherVault.getId());
            teacherTx.setPaymentId(payment.getId());
            teacherTx.setCourseId(payment.getCourse().getCourseid());
            teacherTx.setInitiaterId(payment.getUser().getUserid());

            vaultTxService.createTransaction(teacherTx);

            teacherVault.setAvailableBalance(teacherTx.getBalanceAfter());
            teacherVault.setTotalEarned(teacherVault.getTotalEarned().add(teacherTx.getAmount()));
            teacherVault.setUpdatedAt(Instant.now());
            vaultRepo.save(teacherVault);


            // ------------------------------------------------------------------------------

            VaultTransactionDTO adminTx = new VaultTransactionDTO();
            adminTx.setAmount(amount.multiply(BigDecimal.valueOf(0.20))); // 20
            adminTx.setType(VaultTxType.ENROLLMENT_CREDIT);
            adminTx.setStatus(TxStatus.COMPLETED);
            adminTx.setBalanceAfter(adminVault.getAvailableBalance().add(adminTx.getAmount()));
            adminTx.setCreatedAt(Instant.now());
            adminTx.setVaultId(adminVault.getId());
            adminTx.setPaymentId(payment.getId());
            adminTx.setCourseId(payment.getCourse().getCourseid());
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
