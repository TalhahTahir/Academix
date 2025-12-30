package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.StripeWebhookEvent;

public interface StripeWebhookEventRepo extends JpaRepository<StripeWebhookEvent, Long> {
    boolean existsByProviderEventId(String providerEventId);
}