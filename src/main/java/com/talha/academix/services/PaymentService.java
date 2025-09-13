package com.talha.academix.services;

import java.time.Instant;
import java.util.List;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.dto.PaymentInitiateResponse;
import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.PaymentType;

public interface PaymentService {
    PaymentInitiateResponse initiatePayment(Long userId, Long courseId);

    PaymentDTO getPayment(Long paymentId);

    List<PaymentDTO> getPaymentsByUser(Long userId);

    List<PaymentDTO> getPaymentsByCourse(Long courseId);

    List<PaymentDTO> getPaymentsByType(PaymentType type);

    List<PaymentDTO> getPaymentsCreatedBetween(Instant start, Instant end);

    void markFailed(Long paymentId, String reason);

    void markSucceeded(Long paymentId);

    void markRequiresAction(Long paymentId);

    void markProcessing(Long paymentId);

    void markStatus(Long paymentId, PaymentStatus status);
}