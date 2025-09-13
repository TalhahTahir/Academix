package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.talha.academix.config.StripeConfig;
import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.dto.PaymentInitiateResponse;
import com.talha.academix.enums.PaymentProvider;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Payment;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.PaymentService;
import com.talha.academix.services.StripePaymentDetailService;

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
    private final StripeConfig stripeConfig;
    private final StripePaymentDetailService stripeDetailService;
    private final ModelMapper mapper;

    private static final String CURRENCY = "USD";

    @Override
    public PaymentInitiateResponse initiatePayment(Long userId, Long courseId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        BigDecimal amount = course.getFees();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setCourse(course);
        payment.setAmount(amount);
        payment.setCurrency(CURRENCY);
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setStatus(PaymentStatus.CREATED);
        payment = paymentRepo.save(payment);

        long stripeAmountMinor = amount.movePointRight(2).longValueExact();

        Map<String, String> metadata = new HashMap<>();
        metadata.put("payment_id", payment.getId().toString());
        metadata.put("user_id", user.getUserid().toString());
        metadata.put("course_id", course.getCourseid().toString());

        PaymentIntent intent;
        try {
            intent = stripeConfig.createPaymentIntent(
                    stripeAmountMinor,
                    CURRENCY.toLowerCase(),
                    metadata
            );
        } catch (StripeException e) {
            log.error("Stripe intent creation failed", e);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailedAt(Instant.now());
            paymentRepo.save(payment);
            throw new RuntimeException("Payment initiation failed");
        }

        // persist stripe detail
        stripeDetailService.createForIntent(payment, intent);

        PaymentStatus mapped = stripeDetailService.mapStripeStatus(intent.getStatus(), false);
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