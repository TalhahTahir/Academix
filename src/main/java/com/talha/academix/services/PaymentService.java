package com.talha.academix.services;

import java.util.Date;
import java.util.List;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.dto.WalletDTO;
import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;

public interface PaymentService {
    PaymentDTO processPayment(Long userId, Long courseId);
    PaymentDTO getPaymentById(Long paymentId);
    List<PaymentDTO> getPaymentsByUser(Long userId);
    List<PaymentDTO> getPaymentsByCourse(Long courseId);
    List<PaymentDTO> getPaymentsByType(PaymentType type);
    List<PaymentDTO> getPaymentsBetween(Date start, Date end);
    void markAsPaid(String paymentIntentId);
    void confirmPayment(String transactionId);
    WalletDTO saveTokenizedWallet(Long userId, PaymentMedium medium, String account, String token, String brand, String accountReference);

    PaymentDTO addPayment(PaymentDTO dto);
}