package com.talha.academix.services.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.model.Payment;
import com.talha.academix.model.StripePaymentEvent;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.StripePaymentEventRepo;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.StripePaymentDetailService;
import com.talha.academix.services.StripePaymentEventService;
import com.talha.academix.services.VaultService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StripePaymentEventServiceImpl implements StripePaymentEventService {

    private final StripePaymentEventRepo eventRepo;
    private final PaymentRepo paymentRepo;
    private final StripePaymentDetailService detailService;
    private final EnrollmentService enrollmentService;
    private final VaultService vaultService;

    @Override
    public void processEvent(Event event, boolean signatureValid) {
//
        System.out.println(" 2.1 --- Processing Stripe event: " + event.getId() + " of type " + event.getType());
//
        if (eventRepo.existsByProviderEventId(event.getId())) {
            log.info("Duplicate Stripe event {} ignored", event.getId());
            return;
        }

        PaymentIntent intent = extractPaymentIntent(event);
        if (intent == null) {
            log.warn("Could not resolve PaymentIntent for event {}", event.getId());
            return;
        }

//
System.out.println(" 2.2 --- StripePaymentEventServiceImpl running ");
//

        String paymentIdMeta = intent.getMetadata() != null ? intent.getMetadata().get("payment_id") : null;
        if (paymentIdMeta == null) {
            log.warn("Missing payment_id metadata for intent {} (event {})", intent.getId(), event.getId());
            return;
        }

        Payment payment = paymentRepo.findById(Long.valueOf(paymentIdMeta)).orElse(null);
        if (payment == null) {
            log.warn("Payment entity {} not found (event {})", paymentIdMeta, event.getId());
            return;
        }

        StripePaymentEvent audit = new StripePaymentEvent();
        audit.setPayment(payment);
        audit.setProviderEventId(event.getId());
        audit.setEventType(event.getType());
        audit.setSignatureValid(signatureValid);
        audit.setRawPayload(extractRawPayload(event));
        audit.setReceivedAt(Instant.now());
//
System.out.println(" 2.3 --- StripePaymentEventServiceImpl running ");
//
        try {
            switch (event.getType()) {
                case "payment_intent.processing" -> updateIntent(intent, PaymentStatus.PROCESSING);
                case "payment_intent.succeeded" -> {
                    updateIntent(intent, PaymentStatus.SUCCEEDED);
                    finalizeEnrollmentIfPossible(intent);
                }
                case "payment_intent.payment_failed" -> updateIntent(intent, PaymentStatus.FAILED);
                case "payment_intent.canceled" -> updateIntent(intent, PaymentStatus.CANCELED);
                case "charge.succeeded" -> {
                    // Additional charge enrichment: refetch & update details
                    updateIntent(intent,
                            detailService.mapStripeStatus(intent.getStatus(), true));
                }
                default -> log.debug("Unhandled Stripe event type {}", event.getType());
            }
            audit.setProcessedAt(Instant.now());
        } catch (Exception ex) {
            log.error("Error processing Stripe event {}", event.getId(), ex);
        } finally {
            eventRepo.save(audit);
        }
        
//
System.out.println(" 2.4 --- StripePaymentEventServiceImpl running ");
//
        vaultService.shareDistribution(payment);
//
System.out.println(" 2.5 --- StripePaymentEventServiceImpl running ");
//
    }

    private void updateIntent(PaymentIntent intent, PaymentStatus status) {
        detailService.updateFromIntent(intent, status);
    }

    private void finalizeEnrollmentIfPossible(PaymentIntent intent) {
        try {
            String userId = intent.getMetadata().get("user_id");
            String courseId = intent.getMetadata().get("course_id");
            if (userId != null && courseId != null) {
                enrollmentService.finalizeEnrollment(Long.valueOf(userId), Long.valueOf(courseId));
            }
        } catch (Exception e) {
            log.error("Enrollment finalization failed for intent {}", intent.getId(), e);
        }
    }

    private PaymentIntent extractPaymentIntent(Event event) {
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        try {
            if (obj instanceof PaymentIntent pi) {
                return pi;
            }
            if (obj instanceof Charge charge) {
                if (charge.getPaymentIntent() != null) {
                    return PaymentIntent.retrieve(charge.getPaymentIntent());
                }
            }
        } catch (StripeException e) {
            log.error("Failed retrieving PaymentIntent for event {}", event.getId(), e);
        }
        return null;
    }

    private String extractRawPayload(Event event) {
        return event.getDataObjectDeserializer()
                .getObject()
                .map(StripeObject::toJson)
                .orElse("{}");
    }
}