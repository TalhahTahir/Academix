package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.StripePaymentEvent;

public interface StripePaymentEventRepo extends JpaRepository<StripePaymentEvent, Long> {
    boolean existsByProviderEventId(String providerEventId);
}