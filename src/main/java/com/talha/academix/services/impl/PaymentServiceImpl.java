package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Payment;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepo paymentRepo;
    private final ModelMapper modelMapper;
    @Override
    public PaymentDTO addPayment(PaymentDTO dto) {
        Payment payment = modelMapper.map(dto, Payment.class);
        paymentRepo.save(payment);
        return modelMapper.map(payment, PaymentDTO.class);
        
    }
    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
       Payment payment = paymentRepo.findById(paymentId)
       .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
       return modelMapper.map(payment, PaymentDTO.class);
    }
    @Override
    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        List<Payment> payments = paymentRepo.findByUser(userId);
        return payments.stream()
        .map(payment -> modelMapper.map(payment, PaymentDTO.class))
        .toList();

    }
    @Override
    public List<PaymentDTO> getPaymentsByCourse(Long courseId) {
        List<Payment> payments = paymentRepo.findByCourse(courseId);
        return payments.stream()
        .map(payment -> modelMapper.map(payment, PaymentDTO.class))
        .toList();
    }
    @Override
    public List<PaymentDTO> getPaymentsByType(PaymentType paymentype) {
        List<Payment> payments = paymentRepo.findByType(paymentype);
        return payments.stream()
        .map(payment -> modelMapper.map(payment, PaymentDTO.class))
        .toList();
        
    }

    
}
