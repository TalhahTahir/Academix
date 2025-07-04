package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.model.Course;
import com.talha.academix.model.Wallet;

public interface PaymentService {
PaymentDTO addPayment(PaymentDTO dto); 
PaymentDTO getPaymentById(Long paymentId);
List<PaymentDTO> getPaymentsByUser(Long userId); 
List<PaymentDTO> getPaymentsByCourse(Long courseId);
List<PaymentDTO> getPaymentsByType(PaymentType paymentype); // view incoming vs outgoing

boolean processPayment(Long studentId, Course course, Wallet wallet);

}
