package com.talha.academix.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.PaymentStatus;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.model.Course;
import com.talha.academix.model.Payment;
import com.talha.academix.model.User;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(User user);
    List<Payment> findByCourse(Course course);
    List<Payment> findByPaymentType(PaymentType type);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByCreatedAtBetween(Instant start, Instant end);
    Optional<Payment> findByIdAndUser_Userid(Long id, Long userId);
}