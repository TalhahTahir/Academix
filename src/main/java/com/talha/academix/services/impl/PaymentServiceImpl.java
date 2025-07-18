package com.talha.academix.services.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.enums.Role;
import com.talha.academix.exception.PaymentFailedException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Payment;
import com.talha.academix.model.User;
import com.talha.academix.model.Wallet;
import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;
import com.talha.academix.payment.orchestrator.PaymentOrchestrator;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.WalletRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final WalletRepo walletRepo;
    private final PaymentOrchestrator orchestrator;
    private final ActivityLogService activityLogService;
    private final EnrollmentService enrollmentService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public PaymentDTO processPayment(Long userId, Long courseId) {
        // 1. Load domain objects
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        Wallet wallet = walletRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not set up for user: " + userId));

        // 2. Determine amount and type
        int amount;
        PaymentType type;
        if (user.getRole() == Role.STUDENT) {
            type = PaymentType.INCOMING;
            amount = course.getFees();
        } else if (user.getRole() == Role.TEACHER) {
            type = PaymentType.OUTGOING;
            amount = course.getSalary();
        } else {
            throw new IllegalArgumentException("Invalid role for payment: " + user.getRole());
        }

        // 3. Build a unified request and invoke the gateway
        PaymentRequest req = new PaymentRequest((long) amount, wallet.getAccount(), type, wallet.getMedium());
        PaymentResponse resp = orchestrator.orchestrate(req);

        // 4. Persist initial Payment record
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setCourse(course);
        payment.setAmount(amount);
        payment.setMedium(wallet.getMedium());
        payment.setAccount(wallet.getAccount());
        payment.setPaymentType(type);
        payment.setGatewayTransactionId(resp.getTransactionId());
        payment.setGatewayStatus(resp.getStatusMessage());
        payment.setDate(new Date());
        payment = paymentRepo.save(payment);

        // 5. Log the initiation
        activityLogService.logAction(
                userId,
                ActivityAction.PAYMENT,
                String.format("%s initiated %s payment of %d for course %d (txn=%s)",
                        user.getRole(), type, amount, courseId, resp.getTransactionId()));

        // 6. Drive the continuation based on response
        if (resp.isSuccess()) {
            // fully settled immediately
            markAsPaid(resp.getTransactionId());
        } else if (resp.isRequiresAction()) {
            // front‑end must handle clientSecret (e.g. 3DS challenge)
            // and then call confirmPayment(...) or wait for webhook
        } else {
            // permanent failure
            throw new PaymentFailedException("Payment failed: " + resp.getStatusMessage());
        }

        // 7. Return current PaymentDTO (for requiresAction, clientSecret is in dto)
        PaymentDTO dto = mapper.map(payment, PaymentDTO.class);
        dto.setClientSecret(resp.getClientSecret());
        return dto;
    }

    @Override
    @Transactional
    public void confirmPayment(String transactionId) {
        // called by front‑end after challenge
        markAsPaid(transactionId);
    }

    @Override
    @Transactional
    public void markAsPaid(String transactionId) {
        Payment p = paymentRepo.findByGatewayTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for txn: " + transactionId));

        if (!"succeeded".equalsIgnoreCase(p.getGatewayStatus())) {
            p.setGatewayStatus("succeeded");
            paymentRepo.save(p);

            activityLogService.logAction(
                    p.getUser().getUserid(),
                    ActivityAction.PAYMENT,
                    "Payment confirmed (txn=" + transactionId + ")");

            // trigger enrollment or salary payout
            if (p.getPaymentType() == PaymentType.INCOMING) {
                enrollmentService.finalizeEnrollment(p.getUser().getUserid(), p.getCourse().getCourseid());
            } else {
                // for teacher payouts, you might call payoutService.finalizePayout(...)
            }
        }
    }

    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));
        return mapper.map(payment, PaymentDTO.class);
    }

    @Override
    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return paymentRepo.findByUser(user).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    public List<PaymentDTO> getPaymentsByCourse(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        return paymentRepo.findByCourse(course).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    public List<PaymentDTO> getPaymentsByType(PaymentType type) {
        return paymentRepo.findByPaymentType(type).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    public List<PaymentDTO> getPaymentsBetween(Date start, Date end) {
        return paymentRepo.findByDateBetween(start, end).stream()
                .map(p -> mapper.map(p, PaymentDTO.class))
                .toList();
    }

    @Override
    public PaymentDTO addPayment(PaymentDTO dto) {
        Payment payment = mapper.map(dto, Payment.class);
        payment = paymentRepo.save(payment);
        return mapper.map(payment, PaymentDTO.class);
    }
}
