package com.talha.academix.payment.orchestrator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.talha.academix.dto.WalletDTO;
import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.payment.PaymentHandler;
import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;
import com.talha.academix.services.WalletService;

import lombok.RequiredArgsConstructor;

/**
 * Routes payment requests and webhooks to the appropriate PaymentHandler.
 */
@Component
@RequiredArgsConstructor
public class PaymentOrchestrator {

    private final WalletService walletService; 
    private final List<PaymentHandler> handlers;

    /**
     * Initiates a payment or payout by selecting the correct handler.
     *
     * @param request the unified payment request
     * @return a PaymentResponse carrying success flag, clientSecret, statusMessage, transactionId
     */
public PaymentResponse orchestrate(PaymentRequest request) {
    // If walletId is provided, fetch wallet and set token/account details
    if (request.getWalletId() != null) {
        WalletDTO wallet = walletService.getWalletById(request.getWalletId());
        // If token exists, use token; else use account for first-time
        if (wallet.getToken() != null && !wallet.getToken().isBlank()) {
            request.setToken(wallet.getToken());
        } else {
            request.setToken(null); // Explicitly mark as first time
        }
        request.setAccount(wallet.getAccount());
        request.setMedium(wallet.getMedium());
    }
    PaymentHandler handler = findHandler(request);
    return handler.initiate(request);
}

    /**
     * Processes a webhook callback for the given medium.
     *
     * @param medium the payment medium (e.g. STRIPE, JAZZCASH)
     * @param payload the raw webhook payload (JSON or form data)
     * @param signature any provider‑specific header needed for verification (may be null)
     */
    public void handleWebhook(PaymentMedium medium, String payload, String signature) {
        PaymentHandler handler = handlers.stream()
            .filter(h -> h.supports(new PaymentRequest(null, null, null, medium, null, null)))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(
                "No webhook handler for medium: " + medium));
        handler.handleWebhook(payload, signature);
    }

    /**
     * Utility to fetch a handler for direct use (e.g. in controllers).
     */
    public PaymentHandler getHandlerFor(PaymentMedium medium) {
        return handlers.stream()
        .filter(h -> h.supports(new PaymentRequest(null, null, null, medium, null, null)))

            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(
                "No handler for medium: " + medium));
    }

    /**
     * Finds the PaymentHandler that supports the given request.
     */
    private PaymentHandler findHandler(PaymentRequest request) {
        return handlers.stream()
            .filter(h -> h.supports(request))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(
                "No handler for medium: " + request.getMedium()));
    }
}
