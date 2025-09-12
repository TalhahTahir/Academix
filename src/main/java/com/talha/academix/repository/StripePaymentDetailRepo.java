package com.talha.academix.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.StripePaymentDetail;

public interface StripePaymentDetailRepo extends JpaRepository<StripePaymentDetail, Long> {
    Optional<StripePaymentDetail> findByPaymentIntentId(String intentId);
    boolean existsByPaymentIntentId(String intentId);
}