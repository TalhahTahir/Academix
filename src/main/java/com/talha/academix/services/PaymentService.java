package com.talha.academix.services;

import java.util.Date;
import java.util.List;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.PaymentType;

public interface PaymentService {
    PaymentDTO processPayment(Long userId, Long courseId);
    PaymentDTO getPaymentById(Long paymentId);
    List<PaymentDTO> getPaymentsByUser(Long userId);
    List<PaymentDTO> getPaymentsByCourse(Long courseId);
    List<PaymentDTO> getPaymentsByType(PaymentType type);
    List<PaymentDTO> getPaymentsBetween(Date start, Date end);

    PaymentDTO addPayment(PaymentDTO dto);
}