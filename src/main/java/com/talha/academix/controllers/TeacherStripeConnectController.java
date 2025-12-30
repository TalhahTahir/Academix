package com.talha.academix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.services.impl.TeacherAccountServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stripe/connect")
@RequiredArgsConstructor
public class TeacherStripeConnectController {

    private final TeacherAccountServiceImpl teacherAccountService;

    @PostMapping("/teachers/{teacherId}/onboarding-link")
    public ResponseEntity<String> onboardingLink(@PathVariable Long teacherId) {
        String refreshUrl = "http://localhost:8081/api/stripe/connect/refresh";
        String returnUrl  = "http://localhost:8081/api/stripe/connect/return";
        return ResponseEntity.ok(teacherAccountService.createOnboardingLink(teacherId, refreshUrl, returnUrl));
    }

    @GetMapping("/return")
    public ResponseEntity<String> onboardingReturn() {
        return ResponseEntity.ok("Stripe onboarding completed. You may close this page.");
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> onboardingRefresh() {
        return ResponseEntity.ok("Stripe onboarding was not completed. Please retry.");
    }
}