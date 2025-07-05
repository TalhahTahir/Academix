package com.talha.academix.repository;

import com.talha.academix.model.Payment;
import com.talha.academix.model.User;
import com.talha.academix.model.Course;
import com.talha.academix.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(User user);
    List<Payment> findByCourse(Course course);
    List<Payment> findByPaymentType(PaymentType type);
}
