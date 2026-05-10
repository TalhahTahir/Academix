package com.talha.academix.services;

import java.util.Optional;

import com.talha.academix.enums.TeacherAccountStatus;
import com.talha.academix.model.TeacherAccount;

public interface TeacherAccountService {
    
    TeacherAccount getOrCreateStripeAccountForTeacher(Long teacherId);
    String createOnboardingLink(Long teacherId, String refreshUrl, String returnUrl);
    TeacherAccountStatus syncStatusFromStripe(Long teacherId);
    TeacherAccountStatus syncStatusFromStripe(TeacherAccount ta);
    Optional<String> getOnboardingLinkIfNotOnboarded(Long teacherId, String refreshUrl, String returnUrl);
}
