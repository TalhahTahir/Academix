package com.talha.academix.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.talha.academix.dto.StripeWebhookAck;
import com.talha.academix.services.StripePaymentEventService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final StripePaymentEventService stripePaymentEventService;

    @Value("${stripe.webhook-secret:}")
    private String endpointSecret;

    @PostMapping
    public ResponseEntity<StripeWebhookAck> handle(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader,
            HttpServletRequest request) throws IOException {
//
                System.out.println("1.1 --- Received Stripe webhook: " + payload);
//
        if (endpointSecret == null || endpointSecret.isBlank()) {
            log.error("Stripe webhook secret not configured (property stripe.webhook-secret).");
            return ResponseEntity.internalServerError()
                    .body(new StripeWebhookAck(null, "webhook-secret-missing"));
        }

        Event event;
        boolean signatureValid = true;
//
        System.out.println("1.2 --- Stripe Web Controller running");
//
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature: {}", e.getMessage());
            signatureValid = false;
            // We can return 400 immediately OR still record the event with invalid
            // signature.
            return ResponseEntity.badRequest()
                    .body(new StripeWebhookAck(null, "invalid-signature"));
        }

        stripePaymentEventService.processEvent(event, signatureValid);
//
System.out.println("1.2 --- Stripe Web Controller executed");
//
        return ResponseEntity.ok(new StripeWebhookAck(event.getId(), "processed"));
    }
}