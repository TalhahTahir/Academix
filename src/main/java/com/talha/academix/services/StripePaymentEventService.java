package com.talha.academix.services;

import com.stripe.model.Event;

/**
 * Processes Stripe webhook events (idempotent) and delegates intent updates to
 * StripePaymentDetailService.
 */
public interface StripePaymentEventService {

    /**
     * Process a Stripe event after signature verification.
     *
     * @param event The Stripe Event object
     * @param signatureValid result of signature verification at controller layer
     */
    void processEvent(Event event, boolean signatureValid);
}