package com.talha.academix.services;

import com.stripe.model.PaymentIntent;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.model.Payment;
import com.talha.academix.model.StripePaymentDetail;

/**
 * Handles persistence and synchronization of Stripe-specific intent / charge
 * details, independent from high‑level payment orchestration.
 */
public interface StripePaymentDetailService {

    /**
     * Create a StripePaymentDetail record after a PaymentIntent is first created.
     */
    StripePaymentDetail createForIntent(Payment payment, PaymentIntent intent);

    /**
     * Update stored detail + owning Payment status from a fresh PaymentIntent object.
     * Will also map card brand / last4 and failure information if present.
     */
    void updateFromIntent(PaymentIntent intent, PaymentStatus mappedStatus);

    /**
     * Fetch or throw if not found.
     */
    StripePaymentDetail getByIntentId(String paymentIntentId);

    /**
     * Map a raw Stripe status to internal PaymentStatus (same logic reused for
     * initiation + webhook).
     */
    PaymentStatus mapStripeStatus(String stripeStatus, boolean postConfirm);
}