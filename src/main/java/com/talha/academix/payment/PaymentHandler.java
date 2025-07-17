package com.talha.academix.payment;

import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;

/**
 * Strategy for a single payment gateway.
 */
public interface PaymentHandler {
    /** 
     * Can this handler process the given request (by medium)? 
     */
    boolean supports(PaymentRequest request);

    /**
     * Initiate a payment or payout operation.
     */
    PaymentResponse initiate(PaymentRequest request);

    /**
     * Handle an incoming webhook callback.
     *
     * @param payload   the raw body of the webhook (JSON, form data, etc.)
     * @param signature the gateway‑specific signature header (e.g. Stripe-Signature), or null if unused
     */
    void handleWebhook(String payload, String signature);
}
