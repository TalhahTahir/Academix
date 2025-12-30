package com.talha.academix.exception;

import lombok.Getter;

@Getter
public class StripeOnboardingRequiredException extends RuntimeException {

    private final String onboardingUrl;

    public StripeOnboardingRequiredException(String onboardingUrl) {
        super("Stripe onboarding required");
        this.onboardingUrl = onboardingUrl;
    }
}
