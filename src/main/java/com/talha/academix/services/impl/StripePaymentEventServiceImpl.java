package com.talha.academix.services.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.StripeObject;
import com.stripe.model.Transfer;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.model.Payment;
import com.talha.academix.model.StripePaymentEvent;
import com.talha.academix.model.StripeWebhookEvent;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.StripePaymentEventRepo;
import com.talha.academix.repository.StripeWebhookEventRepo;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.StripePaymentDetailService;
import com.talha.academix.services.StripePaymentEventService;
import com.talha.academix.services.VaultService;
import com.talha.academix.services.WithdrawalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentEventServiceImpl implements StripePaymentEventService {

    private final StripeWebhookEventRepo webhookEventRepo;
    private final StripePaymentEventRepo paymentEventRepo;

    private final PaymentRepo paymentRepo;
    private final StripePaymentDetailService detailService;
    private final EnrollmentService enrollmentService;
    private final VaultService vaultService;
    private final WithdrawalService withdrawalService;

    @Override
    @Transactional
    public void processEvent(Event event, boolean signatureValid) {

        log.info("Processing Stripe event {} of type {}", event.getId(), event.getType());

        // Generic idempotency for ALL Stripe events
        if (webhookEventRepo.existsByProviderEventId(event.getId())) {
            log.info("Duplicate Stripe event {} ignored", event.getId());
            return;
        }

        StripeWebhookEvent genericAudit = new StripeWebhookEvent();
        genericAudit.setProviderEventId(event.getId());
        genericAudit.setEventType(event.getType());
        genericAudit.setSignatureValid(signatureValid);
        genericAudit.setRawPayload(extractRawPayload(event));
        genericAudit.setReceivedAt(Instant.now());
        webhookEventRepo.save(genericAudit);

        try {
            System.out.println("EVENT TYPE: " + event.getType());
            // Route connect events first (they don't have payment_id metadata)
            if (event.getType().startsWith("transfer.")) {
                handleTransferEvent(event);
                genericAudit.setProcessedAt(Instant.now());
                webhookEventRepo.save(genericAudit);
                return;
            }
            if (event.getType().startsWith("tr_")) {
                handleTransferEvent(event);
                genericAudit.setProcessedAt(Instant.now());
                webhookEventRepo.save(genericAudit);
                return;
            }
            if (event.getType().startsWith("payout.")) {
                handlePayoutEvent(event);
                genericAudit.setProcessedAt(Instant.now());
                webhookEventRepo.save(genericAudit);
                return;
            }
            if (event.getType().startsWith("account.")) {
                handleAccountEvent(event);
                genericAudit.setProcessedAt(Instant.now());
                webhookEventRepo.save(genericAudit);
                return;
            }

            // PaymentIntent flow (existing)
            handlePaymentIntentEvent(event, signatureValid);

            genericAudit.setProcessedAt(Instant.now());
            webhookEventRepo.save(genericAudit);

        } catch (Exception ex) {
            log.error("Error processing Stripe event {}", event.getId(), ex);
            // still persist genericAudit without processedAt (already saved)
        }
    }

    private void handleTransferEvent(Event event) {
        Transfer transfer = deserialize(event, Transfer.class);
        if (transfer == null) {
            log.warn("Could not deserialize Transfer for event {}", event.getId());
            return;
        }

        String transferId = transfer.getId();
        switch (event.getType()) {
            case "transfer.created" -> withdrawalService.handleTransferPaid(transferId);
            case "transfer.paid" -> withdrawalService.handleTransferPaid(transferId);
            case "transfer.failed" -> withdrawalService.handleTransferFailed(transferId);
            default -> log.debug("Unhandled transfer event type {}", event.getType());
        }
    }

    private void handlePayoutEvent(Event event) {
        Payout payout = deserialize(event, Payout.class);
        if (payout == null) {
            log.warn("Could not deserialize Payout for event {}", event.getId());
            return;
        }

        String payoutId = payout.getId();
        switch (event.getType()) {
            case "payout.paid" -> withdrawalService.handlePayoutPaid(payoutId);
            case "payout.failed" -> withdrawalService.handlePayoutFailed(payoutId);
            default -> log.debug("Unhandled payout event type {}", event.getType());
        }
    }

    private void handleAccountEvent(Event event) {
        // account.updated events are fired when a Connect account's status changes
        // (e.g., teacher completes onboarding). We just log it for now.
        // The status is synced on-demand in TeacherAccountServiceImpl.syncStatusFromStripe()
        log.info("Received account event: {} for event {}", event.getType(), event.getId());
        // Future: could proactively update TeacherAccount status here
    }

    private void handlePaymentIntentEvent(Event event, boolean signatureValid) {
        // Payment-specific idempotency table (optional but fine to keep)
        if (paymentEventRepo.existsByProviderEventId(event.getId())) {
            log.info("Duplicate payment event {} ignored", event.getId());
            return;
        }

        PaymentIntent intent = extractPaymentIntent(event);
        if (intent == null) {
            log.warn("Could not resolve PaymentIntent for event {}", event.getId());
            return;
        }

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

        try {
            switch (event.getType()) {
                case "payment_intent.processing" -> updateIntent(intent, PaymentStatus.PROCESSING);

                case "payment_intent.succeeded" -> {
                    updateIntent(intent, PaymentStatus.SUCCEEDED);
                    finalizeEnrollmentIfPossible(intent);
                    vaultService.shareDistribution(payment);
                }

                case "payment_intent.payment_failed" -> updateIntent(intent, PaymentStatus.FAILED);
                case "payment_intent.canceled" -> updateIntent(intent, PaymentStatus.CANCELED);

                case "charge.succeeded" -> updateIntent(intent,
                        detailService.mapStripeStatus(intent.getStatus(), true));

                default -> log.debug("Unhandled Stripe event type {}", event.getType());
            }
            audit.setProcessedAt(Instant.now());
        } catch (Exception ex) {
            log.error("Error processing Stripe payment event {}", event.getId(), ex);
        } finally {
            paymentEventRepo.save(audit);
        }
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

    private <T extends StripeObject> T deserialize(Event event, Class<T> clazz) {
        StripeObject obj = event.getDataObjectDeserializer().getObject().orElse(null);
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        return null;
    }
}