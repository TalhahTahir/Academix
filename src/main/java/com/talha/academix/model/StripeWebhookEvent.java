package com.talha.academix.model;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "stripe_webhook_event",
    uniqueConstraints = @UniqueConstraint(name = "uk_stripe_webhook_event_id", columnNames = "provider_event_id"),
    indexes = {
        @Index(name = "idx_stripe_webhook_event_type", columnList = "event_type"),
        @Index(name = "idx_stripe_webhook_event_received_at", columnList = "received_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class StripeWebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="provider_event_id", length=120, nullable=false)
    private String providerEventId;

    @Column(name="event_type", length=80, nullable=false)
    private String eventType;

    @Column(name="signature_valid", nullable=false)
    private boolean signatureValid;

    @Lob
    @Column(name="raw_payload", nullable=false, columnDefinition="LONGTEXT")
    private String rawPayload;

    @Column(name="received_at", nullable=false)
    private Instant receivedAt;

    @Column(name="processed_at")
    private Instant processedAt;
}