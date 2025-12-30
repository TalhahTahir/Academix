package com.talha.academix.services.impl;

import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.talha.academix.enums.Role;
import com.talha.academix.enums.TeacherAccountStatus;
import com.talha.academix.model.TeacherAccount;
import com.talha.academix.model.User;
import com.talha.academix.repository.TeacherAccountRepo;
import com.talha.academix.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherAccountServiceImpl {

    private static final String PLATFORM_COUNTRY = "US";

    private static final String PLATFORM_WEBSITE_URL = null;
    private static final String PLATFORM_PRODUCT_DESCRIPTION =
            "Academix is a learning management platform. Teachers sell courses and receive payouts for enrollments.";

    private final TeacherAccountRepo teacherAccRepo;
    private final UserRepo userRepo;
    private final TeacherAccountPersistenceService persistence; // NEW

    /**
     * IMPORTANT: This method should not be @Transactional because it calls Stripe API.
     * We want the DB save to commit even if Stripe calls fail later.
     */
    public TeacherAccount getOrCreateStripeAccountForTeacher(Long teacherId) {

        Optional<TeacherAccount> existing = teacherAccRepo.findByTeacher_Userid(teacherId);
        if (existing.isPresent()) return existing.get();

        User teacher = userRepo.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a teacher");
        }

        // 1) Create Stripe connected account (external call)
        final Account account;
        try {
            AccountCreateParams.BusinessProfile.Builder businessProfile =
                    AccountCreateParams.BusinessProfile.builder()
                            .setProductDescription(PLATFORM_PRODUCT_DESCRIPTION);

            if (PLATFORM_WEBSITE_URL != null && !PLATFORM_WEBSITE_URL.isBlank()) {
                businessProfile.setUrl(PLATFORM_WEBSITE_URL);
            }

            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setCountry(PLATFORM_COUNTRY)
                    .setEmail(teacher.getEmail())
                    .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                    .setBusinessProfile(businessProfile.build())
                    .setCapabilities(
                            AccountCreateParams.Capabilities.builder()
                                    .setTransfers(
                                            AccountCreateParams.Capabilities.Transfers.builder()
                                                    .setRequested(true)
                                                    .build())
                                    .build())
                    .putMetadata("teacher_id", teacherId.toString())
                    .build();

            account = Account.create(params);
        } catch (Exception e) {
            log.error("Stripe account creation failed for teacherId={}", teacherId, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to create Stripe account", e);
        }

        // 2) Persist mapping in its own transaction (cannot rollback due to later exceptions)
        TeacherAccount ta = new TeacherAccount();
        ta.setTeacher(teacher);
        ta.setStripeAccountId(account.getId());
        ta.setStatus(TeacherAccountStatus.PENDING);

        TeacherAccount saved = persistence.saveNew(ta);
        log.info("TeacherAccount persisted: teacherId={}, acctId={}", teacherId, saved.getStripeAccountId());
        return saved;
    }

    /**
     * Not transactional - calls Stripe API.
     */
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
            log.error("Failed to create onboarding link for teacherId={}, acct={}", teacherId, ta.getStripeAccountId(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to create onboarding link", e);
        }
    }

    /**
     * Not transactional - calls Stripe API.
     * Updates DB in REQUIRES_NEW to avoid rollback cascade.
     */
    public TeacherAccountStatus syncStatusFromStripe(Long teacherId) {
        TeacherAccount ta = teacherAccRepo.findByTeacher_Userid(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stripe account not created"));

        try {
            Account account = Account.retrieve(ta.getStripeAccountId());

            boolean payoutsEnabled = Boolean.TRUE.equals(account.getPayoutsEnabled());
            boolean hasDue = account.getRequirements() != null
                    && account.getRequirements().getCurrentlyDue() != null
                    && !account.getRequirements().getCurrentlyDue().isEmpty();

            boolean restricted = account.getRequirements() != null
                    && account.getRequirements().getDisabledReason() != null
                    && !account.getRequirements().getDisabledReason().isBlank();

            TeacherAccountStatus status;
            if (payoutsEnabled && !hasDue) status = TeacherAccountStatus.COMPLETED;
            else if (restricted) status = TeacherAccountStatus.RESTRICTED;
            else status = TeacherAccountStatus.PENDING;

            ta.setStatus(status);
            persistence.saveUpdate(ta);

            return status;

        } catch (Exception e) {
            log.error("Failed to retrieve Stripe account status for teacherId={}, acct={}",
                    teacherId, ta.getStripeAccountId(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to retrieve Stripe account status", e);
        }
    }

    public Optional<String> getOnboardingLinkIfNotOnboarded(Long teacherId, String refreshUrl, String returnUrl) {
        getOrCreateStripeAccountForTeacher(teacherId);
        TeacherAccountStatus status = syncStatusFromStripe(teacherId);

        if (status == TeacherAccountStatus.COMPLETED) return Optional.empty();
        return Optional.of(createOnboardingLink(teacherId, refreshUrl, returnUrl));
    }
}