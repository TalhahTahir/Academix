package com.talha.academix.services.impl;

import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.talha.academix.model.TeacherAccount;
import com.talha.academix.enums.Role;
import com.talha.academix.enums.TeacherAccountStatus;
import com.talha.academix.model.User;
import com.talha.academix.repository.TeacherAccountRepo;
import com.talha.academix.repository.UserRepo;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TeacherAccountServiceImpl {

    private static final String PLATFORM_COUNTRY = "US";

    // If you have a real website, set it here; otherwise keep null and use product_description.
    private static final String PLATFORM_WEBSITE_URL = null; // e.g. "https://academix.com"

    // Alternate to website (recommended if you don't have a public site yet)
    private static final String PLATFORM_PRODUCT_DESCRIPTION =
            "Academix is a learning management platform. Teachers sell courses and receive payouts for enrollments.";

    private final TeacherAccountRepo teacherAccRepo;
    private final UserRepo userRepo;

    @Transactional
    public TeacherAccount getOrCreateStripeAccountForTeacher(Long teacherId) {
        return teacherAccRepo.findByTeacher_Userid(teacherId)
                .orElseGet(() -> {
                    User teacher = userRepo.findById(teacherId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found"));

                    if (teacher.getRole() != Role.TEACHER) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a teacher");
                    }

                    try {
                        // Build business profile (use URL if you have it; otherwise product_description)
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

    // ... keep the rest of your service as-is

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

    @Transactional
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
            if (payoutsEnabled && !hasDue)
                status = TeacherAccountStatus.COMPLETED;
            else if (restricted)
                status = TeacherAccountStatus.RESTRICTED;
            else
                status = TeacherAccountStatus.PENDING;

            ta.setStatus(status);
            teacherAccRepo.save(ta);

            return status;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to retrieve Stripe account status", e);
        }
    }

    /**
     * LAZY_ON_WITHDRAW helper:
     * If teacher isn't onboarded, return a fresh onboarding link to complete first.
     */
    @Transactional
    public Optional<String> getOnboardingLinkIfNotOnboarded(Long teacherId, String refreshUrl, String returnUrl) {
        // Ensure account exists
        getOrCreateStripeAccountForTeacher(teacherId);

        TeacherAccountStatus status = syncStatusFromStripe(teacherId);
        if (status == TeacherAccountStatus.COMPLETED) {
            return Optional.empty();
        }
        return Optional.of(createOnboardingLink(teacherId, refreshUrl, returnUrl));
    }

    @Transactional
    public void requireOnboardedForWithdrawal(Long teacherId) {
        TeacherAccountStatus status = syncStatusFromStripe(teacherId);
        if (status != TeacherAccountStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Teacher is not onboarded to Stripe");
        }
    }
}