// PaymentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.exception.PaymentFailedException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Payment;
import com.talha.academix.model.User;
import com.talha.academix.model.Wallet;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.repository.WalletRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.PaymentGatewayService;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final WalletRepo walletRepo;
    private final PaymentGatewayService gateway;
    private ActivityLogService activityLogService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public PaymentDTO processPayment(Long userId, Long courseId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        Wallet wallet = walletRepo.findByUser(user)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not set up for user: " + userId));

        boolean approved = gateway.charge(wallet.getMedium(), wallet.getAccount(), course.getFees());
        if (!approved) {
            throw new PaymentFailedException("External payment declined for user " + userId);
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setCourse(course);
        payment.setAmount(course.getFees());
        payment.setMedium(wallet.getMedium());
        payment.setAccount(wallet.getAccount());
        payment.setPaymentType(PaymentType.INCOMING);
        payment.setDate(new Date());

        activityLogService.logAction(
         userId,
         ActivityAction.PAYMENT,
         "User " + userId + " paid " + payment.getAmount() + " for Course " + courseId
        );

        payment = paymentRepo.save(payment);
        return mapper.map(payment, PaymentDTO.class);
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
        paymentRepo.save(payment);
        return mapper.map(payment, PaymentDTO.class);
    }
}
