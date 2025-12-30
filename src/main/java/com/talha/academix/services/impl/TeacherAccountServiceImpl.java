package com.talha.academix.services.impl;

import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.talha.academix.model.TeacherAccount;
import com.talha.academix.enums.TeacherAccountStatus;
import com.talha.academix.model.User;
import com.talha.academix.repository.TeacherAccountRepo;
import com.talha.academix.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TeacherAccountServiceImpl {

    private final TeacherAccountRepo teacherAccRepo;
    private final UserRepo userRepo;

    /**
     * Create Stripe account ONCE per teacher; subsequent calls return the existing record.
     */
    @Transactional
    public TeacherAccount getOrCreateStripeAccountForTeacher(Long teacherId) {
        return teacherAccRepo.findByTeacher_Id(teacherId)
                .orElseGet(() -> {
                    User teacher = userRepo.findById(teacherId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));

                    try {
                        AccountCreateParams params = AccountCreateParams.builder()
                                .setType(AccountCreateParams.Type.EXPRESS)
                                .build();

                        Account account = Account.create(params);

                        TeacherAccount ta = new TeacherAccount();
                        ta.setTeacher(teacher);
                        ta.setStripeAccountId(account.getId());
                        ta.setStatus(TeacherAccountStatus.PENDING);

                        return teacherAccRepo.save(ta);
                    } catch (Exception e) {
                        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to create Stripe account", e);
                    }
                });
    }

    /**
     * Generate onboarding link MANY times.
     */
    @Transactional
    public String createOnboardingLink(Long teacherId, String refreshUrl, String returnUrl) {
        TeacherAccount ta = getOrCreateStripeAccountForTeacher(teacherId);

        try {
            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(ta.getStripeAccountId())
                    .setRefreshUrl(refreshUrl)
                    .setReturnUrl(returnUrl)
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink link = AccountLink.create(params);
            return link.getUrl();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to create onboarding link", e);
        }
    }

    /**
     * Sync status from Stripe and persist it.
     * - COMPLETED when payouts are enabled (typical “onboarded” signal for withdrawals)
     * - RESTRICTED when Stripe reports a disabled reason
     * - otherwise PENDING
     */
    @Transactional
    public TeacherAccountStatus syncStatusFromStripe(Long teacherId) {
        TeacherAccount ta = teacherAccRepo.findByTeacher_Id(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stripe account not created"));

        try {
            Account account = Account.retrieve(ta.getStripeAccountId());

            boolean payoutsEnabled = Boolean.TRUE.equals(account.getPayoutsEnabled());
            boolean restricted = account.getRequirements() != null
                    && account.getRequirements().getDisabledReason() != null
                    && !account.getRequirements().getDisabledReason().isBlank();

            TeacherAccountStatus status =
                    payoutsEnabled ? TeacherAccountStatus.COMPLETED :
                            (restricted ? TeacherAccountStatus.RESTRICTED : TeacherAccountStatus.PENDING);

            ta.setStatus(status);
            teacherAccRepo.save(ta);

            return status;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to retrieve Stripe account status", e);
        }
    }

    /**
     * Call this before allowing withdrawal.
     */
    @Transactional
    public void requireOnboardedForWithdrawal(Long teacherId) {
        TeacherAccountStatus status = syncStatusFromStripe(teacherId);
        if (status != TeacherAccountStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Teacher is not onboarded to Stripe");
        }
    }
}