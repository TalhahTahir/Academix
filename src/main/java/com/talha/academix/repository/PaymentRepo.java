package com.talha.academix.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.PaymentType;
import com.talha.academix.model.Course;
import com.talha.academix.model.Payment;
import com.talha.academix.model.User;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByUser(User user);
    List<Payment> findByCourse(Course course);
    List<Payment> findByPaymentType(PaymentType type);

    List<Payment> findByDateBetween(Date start, Date end);

    Optional<Payment> findByGatewayTransactionId(String intentId);

}
