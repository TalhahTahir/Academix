package com.talha.academix.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.PaymentInitiateRequest;
import com.talha.academix.dto.PaymentInitiateResponse;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${stripe.publishable-key}")
    private String publishableKey;
    
    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitiateResponse> initiate(@RequestBody PaymentInitiateRequest req) {
        return ResponseEntity.ok(
                paymentService.initiatePayment(req.getUserId(), req.getCourseId())
        );
    }

        @GetMapping("/config/publishable-key")
    public String publishableKey() {
        return publishableKey;
    }
}