package com.talha.academix.payment;

import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;

public interface PaymentHandler {
     /** Which medium this handler supports */
    boolean supports(PaymentRequest request);

    /** True if this gateway confirms immediately (sync) */
    boolean isSynchronous();

    /** Initiate a payment or payout */
    PaymentResponse initiate(PaymentRequest request);

    /** Handle an incoming webhook payload (async confirmation) */
    void handleWebhook(String payload);
}
