package com.talha.academix.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.payment.orchestrator.PaymentOrchestrator;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentOrchestrator orchestrator;

    @PostMapping("/{medium}")
    public ResponseEntity<String> handleWebhook(
            @PathVariable("medium") PaymentMedium medium,
            @RequestHeader(value = "Stripe-Signature", required = false) String sig,
            @RequestBody String payload) {

        try {
            orchestrator.handleWebhook(medium, payload, sig);
            return ResponseEntity.ok("Received");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Webhook Error: " + e.getMessage());
        }
    }
}

