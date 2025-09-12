package com.talha.academix.services.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Payment;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.UserRepo;
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
    private final ActivityLogService activityLogService;
    private final EnrollmentService enrollmentService;
    private final ModelMapper mapper;

    

    @Override
    public PaymentDTO processPayment(Long userId, Long courseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPayment'");
    }

    @Override
    public void markAsPaid(String paymentIntentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'markAsPaid'");
    }

    @Override
    public void confirmPayment(String transactionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'confirmPayment'");
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
