package com.talha.academix.controllers;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentDTO addPayment(@RequestBody PaymentDTO dto) {
        return paymentService.addPayment(dto);
    }

    @GetMapping("/{paymentId}")
    public PaymentDTO getPaymentById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping("/user/{userId}")
    public List<PaymentDTO> getPaymentsByUser(@PathVariable Long userId) {
        return paymentService.getPaymentsByUser(userId);
    }

    @GetMapping("/course/{courseId}")
    public List<PaymentDTO> getPaymentsByCourse(@PathVariable Long courseId) {
        return paymentService.getPaymentsByCourse(courseId);
    }

    @GetMapping("/type/{type}")
    public List<PaymentDTO> getPaymentsByType(@PathVariable PaymentType type) {
        return paymentService.getPaymentsByType(type);
    }
}
