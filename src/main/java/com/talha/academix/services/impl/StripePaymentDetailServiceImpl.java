package com.talha.academix.services.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.model.PaymentIntent;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Payment;
import com.talha.academix.model.StripePaymentDetail;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.StripePaymentDetailRepo;
import com.talha.academix.services.StripePaymentDetailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StripePaymentDetailServiceImpl implements StripePaymentDetailService {

    private final StripePaymentDetailRepo detailRepo;
    private final PaymentRepo paymentRepo;

    @Override
    public StripePaymentDetail createForIntent(Payment payment, PaymentIntent intent) {
        StripePaymentDetail detail = new StripePaymentDetail();
        detail.setPayment(payment);
        detail.setPaymentIntentId(intent.getId());
        detail.setLatestProviderStatus(intent.getStatus());
        detail.setRawLatestIntent(intent.toJson());
        return detailRepo.save(detail);
    }

    @Override
    public void updateFromIntent(PaymentIntent intent, PaymentStatus mappedStatus) {
        String intentId = intent.getId();

        StripePaymentDetail detail = detailRepo.findByPaymentIntentId(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("Stripe detail not found for intent " + intentId));

        Payment payment = detail.getPayment();

        detail.setLatestProviderStatus(intent.getStatus());
        detail.setPaymentMethodId(intent.getPaymentMethod());

        if (intent.getLatestChargeObject() != null) {
            var charge = intent.getLatestChargeObject();
            detail.setLatestChargeId(charge.getId());

            if (charge.getPaymentMethodDetails() != null &&
                charge.getPaymentMethodDetails().getCard() != null) {
                var card = charge.getPaymentMethodDetails().getCard();
                detail.setCardBrand(card.getBrand());
                detail.setCardLast4(card.getLast4());
            }
        }

        if (intent.getLastPaymentError() != null) {
            detail.setFailureCode(intent.getLastPaymentError().getCode());
            detail.setFailureMessage(intent.getLastPaymentError().getMessage());
        }

        detail.setRawLatestIntent(intent.toJson());
        detailRepo.save(detail);

        payment.setStatus(mappedStatus);
        switch (mappedStatus) {
            case SUCCEEDED -> payment.setSucceededAt(Instant.now());
            case FAILED -> payment.setFailedAt(Instant.now());
            case CANCELED -> payment.setCanceledAt(Instant.now());
            case REFUNDED -> payment.setRefundedAt(Instant.now());
            default -> {}
        }
        paymentRepo.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public StripePaymentDetail getByIntentId(String paymentIntentId) {
        return detailRepo.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Stripe detail not found for intent " + paymentIntentId));
    }

    @Override
    public PaymentStatus mapStripeStatus(String stripeStatus, boolean postConfirm) {
        return switch (stripeStatus) {
            case "requires_action", "requires_confirmation" -> PaymentStatus.REQUIRES_ACTION;
            case "processing" -> PaymentStatus.PROCESSING;
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "canceled" -> PaymentStatus.CANCELED;
            case "requires_payment_method" ->
                    postConfirm ? PaymentStatus.FAILED : PaymentStatus.PENDING;
            default -> PaymentStatus.PENDING;
        };
    }
}