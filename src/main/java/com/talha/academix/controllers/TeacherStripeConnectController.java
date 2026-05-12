package com.talha.academix.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.services.TeacherAccountService;

@RestController
@RequestMapping("/api/stripe/connect")
public class TeacherStripeConnectController {

    private final TeacherAccountService teacherAccountService;

    @Value("${app.stripe.connect.refresh-url}")
    private String connectRefreshUrl;

    @Value("${app.stripe.connect.return-url}")
    private String connectReturnUrl;

    public TeacherStripeConnectController(TeacherAccountService teacherAccountService) {
        this.teacherAccountService = teacherAccountService;
    }

    @PostMapping("/teachers/{teacherId}/onboarding-link")
    public ResponseEntity<String> onboardingLink(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherAccountService.createOnboardingLink(teacherId, connectRefreshUrl, connectReturnUrl));
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