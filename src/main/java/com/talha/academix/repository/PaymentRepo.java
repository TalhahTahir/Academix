package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.PaymentType;
import com.talha.academix.model.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    public List<Payment> findByUser(Long userId);

    public List<Payment> findByCourse(Long courseId);

    public List<Payment> findByType(PaymentType paymentype);
    
}
