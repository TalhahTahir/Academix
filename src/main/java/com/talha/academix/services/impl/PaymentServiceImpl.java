package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.param.PaymentIntentCreateParams;
import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.dto.PaymentInitiateResponse;
import com.talha.academix.enums.PaymentProvider;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Payment;
import com.talha.academix.model.StripePaymentDetail;
import com.talha.academix.model.StripePaymentEvent;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.StripePaymentDetailRepo;
import com.talha.academix.repository.StripePaymentEventRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final StripePaymentDetailRepo stripeDetailRepo;
    private final StripePaymentEventRepo stripeEventRepo;
    private final EnrollmentService enrollmentService;
    private final ModelMapper mapper;

    private static final String CURRENCY = "usd";

    @Override
    public PaymentInitiateResponse initiatePayment(Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        BigDecimal amount = course.getFees(); // adapt

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setCourse(course);
        payment.setAmount(amount);
        payment.setCurrency(CURRENCY.toUpperCase());
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setStatus(PaymentStatus.CREATED);
        payment = paymentRepo.save(payment);

        long stripeAmountInMinor = amount.movePointRight(2).longValueExact();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(stripeAmountInMinor)
                .setCurrency(CURRENCY)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .putMetadata("payment_id", payment.getId().toString())
                .putMetadata("user_id", user.getUserid().toString())
                .putMetadata("course_id", course.getCourseid().toString())
                .build();

        PaymentIntent intent;
        try {
            intent = PaymentIntent.create(params);
        } catch (StripeException e) {
            log.error("Stripe create intent failed", e);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailedAt(Instant.now());
            paymentRepo.save(payment);
            throw new RuntimeException("Payment initiation failed");
        }

        StripePaymentDetail detail = new StripePaymentDetail();
        detail.setPayment(payment);
        detail.setPaymentIntentId(intent.getId());
        detail.setLatestProviderStatus(intent.getStatus());
        detail.setRawLatestIntent(intent.toJson());
        stripeDetailRepo.save(detail);

        PaymentStatus mapped = mapStripeStatus(intent.getStatus(), false);
        payment.setStatus(mapped);
        paymentRepo.save(payment);

        boolean requiresAction = mapped == PaymentStatus.REQUIRES_ACTION;

        return PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .clientSecret(intent.getClientSecret())
                .status(payment.getStatus())
                .requiresAction(requiresAction)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPayment(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        return mapper.map(payment, PaymentDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return paymentRepo.findByUser(user).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByCourse(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        return paymentRepo.findByCourse(course).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByType(PaymentType type) {
        return paymentRepo.findByPaymentType(type).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsCreatedBetween(Instant start, Instant end) {
        return paymentRepo.findByCreatedAtBetween(start, end).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    public void handleStripeEvent(Event event) {
        if (stripeEventRepo.existsByProviderEventId(event.getId())) {
            log.info("Duplicate Stripe event {} ignored", event.getId());
            return;
        }

        StripePaymentEvent audit = new StripePaymentEvent();
        audit.setProviderEventId(event.getId());
        audit.setEventType(event.getType());
        audit.setSignatureValid(true); // set after signature verification in controller
        audit.setRawPayload(event.getDataObjectDeserializer().getObject().map(StripeObject::toJson).orElse(null));
        audit.setReceivedAt(Instant.now());

        try {
            switch (event.getType()) {
                case "payment_intent.processing" -> onIntentProcessing(event);
                case "payment_intent.succeeded" -> onIntentSucceeded(event);
                case "payment_intent.payment_failed" -> onIntentFailed(event);
                case "charge.succeeded" -> onChargeSucceeded(event);
                case "payment_intent.canceled" -> onIntentCanceled(event);
                default -> log.debug("Unhandled Stripe event type {}", event.getType());
            }
            audit.setProcessedAt(Instant.now());
        } catch (Exception ex) {
            log.error("Error processing event {}", event.getId(), ex);
        } finally {
            stripeEventRepo.save(audit);
        }
    }

    private void onIntentProcessing(Event event) throws StripeException {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseGet(() -> retrieveIntent(event));
        updateFromIntent(intent, PaymentStatus.PROCESSING);
    }

    private void onIntentSucceeded(Event event) throws StripeException {
        PaymentIntent intent = (PaymentIntent) deserialize(event);
        updateFromIntent(intent, PaymentStatus.SUCCEEDED);

        // finalize enrollment (id from metadata)
        String userId = intent.getMetadata().get("user_id");
        String courseId = intent.getMetadata().get("course_id");
        if (userId != null && courseId != null) {
            try {
                enrollmentService.finalizeEnrollment(Long.valueOf(userId), Long.valueOf(courseId));
            } catch (Exception e) {
                log.error("Enrollment finalize failed for payment metadata user {} course {}", userId, courseId, e);
            }
        }
    }

    private void onIntentFailed(Event event) throws StripeException {
        PaymentIntent intent = (PaymentIntent) deserialize(event);
        updateFromIntent(intent, PaymentStatus.FAILED);
    }

    private void onIntentCanceled(Event event) throws StripeException {
        PaymentIntent intent = (PaymentIntent) deserialize(event);
        updateFromIntent(intent, PaymentStatus.CANCELED);
    }

    private void onChargeSucceeded(Event event) {
        // Optionally parse Charge for card brand/last4
        StripeObject obj = event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);
        // Could fetch related PaymentIntent id via charge.getPaymentIntent()
        // and update StripePaymentDetail with brand/last4 if not already set.
    }

    private StripeObject deserialize(Event event) {
        return event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalStateException("Unable to deserialize event object"));
    }

    private PaymentIntent retrieveIntent(Event event) {
        try {
            String intentId = event.getDataObjectDeserializer()
                    .getObject()
                    .map(obj -> ((PaymentIntent) obj).getId())
                    .orElseThrow(() -> new IllegalStateException("Unable to retrieve PaymentIntent ID"));
            return PaymentIntent.retrieve(intentId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve PaymentIntent", e);
        }
    }

    private void updateFromIntent(PaymentIntent intent, PaymentStatus mappedStatus) {
        String intentId = intent.getId();

        StripePaymentDetail detail = stripeDetailRepo.findByPaymentIntentId(intentId)
                .orElseThrow(() -> new ResourceNotFoundException("Stripe detail not found for intent " + intentId));

        Payment payment = detail.getPayment();

        detail.setLatestProviderStatus(intent.getStatus());
        detail.setPaymentMethodId(intent.getPaymentMethod());
        if (intent.getLatestChargeObject() != null) {
            detail.setLatestChargeId(intent.getLatestChargeObject().getId());
            if (intent.getLatestChargeObject().getPaymentMethodDetails() != null &&
                    intent.getLatestChargeObject().getPaymentMethodDetails().getCard() != null) {
                var card = intent.getLatestChargeObject().getPaymentMethodDetails().getCard();
                detail.setCardBrand(card.getBrand());
                detail.setCardLast4(card.getLast4());
            }
        }
        if (intent.getLatestChargeObject() != null) {
            var charge = intent.getLatestChargeObject();
            if (charge.getPaymentMethodDetails() != null && charge.getPaymentMethodDetails().getCard() != null) {
                detail.setCardBrand(charge.getPaymentMethodDetails().getCard().getBrand());
                detail.setCardLast4(charge.getPaymentMethodDetails().getCard().getLast4());
            }
        }

        if (intent.getLastPaymentError() != null) {
            detail.setFailureCode(intent.getLastPaymentError().getCode());
            detail.setFailureMessage(intent.getLastPaymentError().getMessage());
        }

        detail.setRawLatestIntent(intent.toJson());
        stripeDetailRepo.save(detail);

        payment.setStatus(mappedStatus);
        switch (mappedStatus) {
            case SUCCEEDED -> payment.setSucceededAt(Instant.now());
            case FAILED -> payment.setFailedAt(Instant.now());
            case CANCELED -> payment.setCanceledAt(Instant.now());
            default -> {
            }
        }
        paymentRepo.save(payment);
    }

    private PaymentStatus mapStripeStatus(String stripeStatus, boolean postConfirm) {
        return switch (stripeStatus) {
            case "requires_action", "requires_confirmation" -> PaymentStatus.REQUIRES_ACTION;
            case "processing" -> PaymentStatus.PROCESSING;
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "canceled" -> PaymentStatus.CANCELED;
            case "requires_payment_method" -> postConfirm ? PaymentStatus.FAILED : PaymentStatus.PENDING;
            default -> PaymentStatus.PENDING;
        };
    }

    @Override
    public void markFailed(Long paymentId, String reason) {
        Payment p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        p.setStatus(PaymentStatus.FAILED);
        p.setFailedAt(Instant.now());
        paymentRepo.save(p);
        log.warn("Payment {} failed: {}", paymentId, reason);
    }

    @Override
    public void markSucceeded(Long paymentId) {
        markStatus(paymentId, PaymentStatus.SUCCEEDED);
    }

    @Override
    public void markRequiresAction(Long paymentId) {
        markStatus(paymentId, PaymentStatus.REQUIRES_ACTION);
    }

    @Override
    public void markProcessing(Long paymentId) {
        markStatus(paymentId, PaymentStatus.PROCESSING);
    }

    @Override
    public void markStatus(Long paymentId, PaymentStatus status) {
        Payment p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        p.setStatus(status);
        if (status == PaymentStatus.SUCCEEDED)
            p.setSucceededAt(Instant.now());
        paymentRepo.save(p);
    }
}