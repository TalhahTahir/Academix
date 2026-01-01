package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.exception.StripeException;
import com.talha.academix.dto.VaultTransactionDTO;
import com.talha.academix.dto.WithdrawalDTO;
import com.talha.academix.dto.WithdrawalRequestDTO;
import com.talha.academix.enums.Role;
import com.talha.academix.enums.TxReferenceType;
import com.talha.academix.enums.TxStatus;
import com.talha.academix.enums.VaultTxType;
import com.talha.academix.enums.WithdrawalKind;
import com.talha.academix.enums.WithdrawalStatus;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.StripeOnboardingRequiredException;
import com.talha.academix.model.User;
import com.talha.academix.model.Vault;
import com.talha.academix.model.VaultTransaction;
import com.talha.academix.model.Withdrawal;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.VaultRepo;
import com.talha.academix.repository.VaultTransactionRepo;
import com.talha.academix.repository.WithdrawalRepo;
import com.talha.academix.services.StripeConnectPayoutService;
import com.talha.academix.services.VaultTransactionService;
import com.talha.academix.services.WithdrawalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {

    private static final BigDecimal MIN_WITHDRAWAL = new BigDecimal("10.00");
    private static final String CURRENCY = "USD";

    // For now hardcoded; later move to application.properties
    private static final String CONNECT_REFRESH_URL = "http://localhost:8081/api/stripe/connect/refresh";
    private static final String CONNECT_RETURN_URL  = "http://localhost:8081/api/stripe/connect/return";

    private final WithdrawalRepo withdrawalRepo;
    private final VaultRepo vaultRepo;
    private final VaultTransactionRepo vaultTxRepo;
    private final UserRepo userRepo;
    private final TeacherAccountServiceImpl teacherAccountService;
    private final StripeConnectPayoutService stripeConnectPayoutService;
    private final VaultTransactionService vaultTransactionService;
    private final ModelMapper mapper;

    @Override
    public WithdrawalDTO requestWithdrawal(WithdrawalRequestDTO req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.TEACHER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin/teacher can withdraw");
        }

        Vault vault = vaultRepo.findByUser_Userid(user.getUserid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vault not found"));

        BigDecimal amount = req.getAmount();

        if (amount == null || amount.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be > 0");
        }
        if (amount.compareTo(MIN_WITHDRAWAL) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum withdrawal is $10");
        }
        if (amount.compareTo(vault.getAvailableBalance()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount exceeds available balance");
        }

        // No multiple pending withdrawals
        if (vault.getPendingWithdrawal() != null && vault.getPendingWithdrawal().compareTo(BigDecimal.ZERO) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A withdrawal is already in progress");
        }
        boolean hasProcessing = withdrawalRepo.existsByVault_IdAndStatusIn(
                vault.getId(),
                List.of(WithdrawalStatus.REQUESTED, WithdrawalStatus.PROCESSING)
        );
        if (hasProcessing) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A withdrawal is already in progress");
        }

        WithdrawalKind kind = (user.getRole() == Role.ADMIN) ? WithdrawalKind.ADMIN : WithdrawalKind.TEACHER;

        // LAZY onboarding: if teacher not onboarded, respond with onboarding URL and do NOT lock funds
        if (kind == WithdrawalKind.TEACHER) {
            Optional<String> onboardingUrl = teacherAccountService.getOnboardingLinkIfNotOnboarded(
                    user.getUserid(), CONNECT_REFRESH_URL, CONNECT_RETURN_URL
            );
            if (onboardingUrl.isPresent()) {
                throw new StripeOnboardingRequiredException(onboardingUrl.get());
            }
        }

        // Lock funds first
        vault.setAvailableBalance(vault.getAvailableBalance().subtract(amount));
        vault.setPendingWithdrawal(vault.getPendingWithdrawal().add(amount));
        vault.setUpdatedAt(Instant.now());
        vaultRepo.save(vault);

        Withdrawal withdrawal = Withdrawal.builder()
                .vault(vault)
                .requestedBy(user)
                .kind(kind)
                .amount(amount)
                .status(WithdrawalStatus.PROCESSING)
                .requestedAt(Instant.now())
                .build();
        withdrawal = withdrawalRepo.save(withdrawal);

        // Ledger entry (pending)
        VaultTransactionDTO tx = new VaultTransactionDTO();
        tx.setVaultId(vault.getId());
        tx.setAmount(amount);
        tx.setType(kind == WithdrawalKind.ADMIN ? VaultTxType.PAYOUT : VaultTxType.TRANSFER);
        tx.setStatus(TxStatus.PENDING);
        tx.setInitiaterId(user.getUserid());
        tx.setReferenceType(TxReferenceType.WITHDRAWAL);
        tx.setReferenceId(withdrawal.getId());
        tx.setBalanceAfter(vault.getAvailableBalance());
        tx.setCreatedAt(Instant.now());
        vaultTransactionService.createTransaction(tx);

        // Now call Stripe
        try {
            long amountMinor = amount.movePointRight(2).longValueExact();

            String providerObjectId;
            if (kind == WithdrawalKind.ADMIN) {
                providerObjectId = stripeConnectPayoutService.createPlatformPayout(amountMinor, CURRENCY);
            } else {
                String acctId = teacherAccountService.getOrCreateStripeAccountForTeacher(user.getUserid())
                        .getStripeAccountId();
                providerObjectId = stripeConnectPayoutService.createTransferToConnectedAccount(acctId, amountMinor, CURRENCY);
            }

            withdrawal.setProviderObjectId(providerObjectId);
            withdrawalRepo.save(withdrawal);

            return mapper.map(withdrawal, WithdrawalDTO.class);

        } catch (StripeException ex) {
            failWithdrawalAndRelease(withdrawal.getId(), "Stripe failure: " + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Stripe withdrawal failed");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WithdrawalDTO getById(Long withdrawalId) {
        Withdrawal w = withdrawalRepo.findById(withdrawalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Withdrawal not found"));
        return mapper.map(w, WithdrawalDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List getByUser(Long userId) {
        return withdrawalRepo.findAllByRequestedBy_Userid(userId)
                .stream()
                .map(w -> mapper.map(w, WithdrawalDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public void handleTransferPaid(String transferId) {
        withdrawalRepo.findByProviderObjectId(transferId).ifPresentOrElse(
                this::finalizePaid,
                () -> log.debug("Transfer {} not associated with any withdrawal (might be external)", transferId)
        );
    }

    @Override
    @Transactional
    public void handleTransferFailed(String transferId) {
        withdrawalRepo.findByProviderObjectId(transferId).ifPresentOrElse(
                w -> failWithdrawalAndRelease(w.getId(), "transfer.failed"),
                () -> log.debug("Transfer {} not associated with any withdrawal (might be external)", transferId)
        );
    }

    @Override
    @Transactional
    public void handlePayoutPaid(String payoutId) {
        withdrawalRepo.findByProviderObjectId(payoutId).ifPresentOrElse(
                this::finalizePaid,
                () -> log.debug("Payout {} not associated with any withdrawal (might be external)", payoutId)
        );
    }

    @Override
    @Transactional
    public void handlePayoutFailed(String payoutId) {
        withdrawalRepo.findByProviderObjectId(payoutId).ifPresentOrElse(
                w -> failWithdrawalAndRelease(w.getId(), "payout.failed"),
                () -> log.debug("Payout {} not associated with any withdrawal (might be external)", payoutId)
        );
    }

    private void finalizePaid(Withdrawal w) {
        if (w.getStatus() == WithdrawalStatus.PAID) return;

        Vault vault = w.getVault();
        BigDecimal amount = w.getAmount();

        vault.setPendingWithdrawal(vault.getPendingWithdrawal().subtract(amount));
        vault.setTotalWithdrawn(vault.getTotalWithdrawn().add(amount));
        vault.setUpdatedAt(Instant.now());
        vaultRepo.save(vault);

        w.setStatus(WithdrawalStatus.PAID);
        w.setProcessedAt(Instant.now());
        withdrawalRepo.save(w);

        VaultTransaction tx = vaultTxRepo.findByReferenceTypeAndReferenceId(TxReferenceType.WITHDRAWAL, w.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No transaction record with given ref.Type & ref.Id"));

        tx.setStatus(TxStatus.COMPLETED);
        vaultTxRepo.save(tx);
    }

    private void failWithdrawalAndRelease(Long withdrawalId, String reason) {
        Withdrawal w = withdrawalRepo.findById(withdrawalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Withdrawal not found"));

        if (w.getStatus() == WithdrawalStatus.FAILED) return;
        if (w.getStatus() == WithdrawalStatus.PAID) return;

        Vault vault = w.getVault();
        BigDecimal amount = w.getAmount();

        vault.setPendingWithdrawal(vault.getPendingWithdrawal().subtract(amount));
        vault.setAvailableBalance(vault.getAvailableBalance().add(amount));
        vault.setUpdatedAt(Instant.now());
        vaultRepo.save(vault);

        w.setStatus(WithdrawalStatus.FAILED);
        w.setProcessedAt(Instant.now());
        withdrawalRepo.save(w);

        VaultTransaction tx = vaultTxRepo.findByReferenceTypeAndReferenceId(TxReferenceType.WITHDRAWAL, w.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No transaction record with given ref.Type & ref.Id"));

        tx.setStatus(TxStatus.FAILED);
        vaultTxRepo.save(tx);
    }
}