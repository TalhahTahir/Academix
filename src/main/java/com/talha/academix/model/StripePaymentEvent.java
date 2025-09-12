package com.talha.academix.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="stripe_payment_event",
       uniqueConstraints = @UniqueConstraint(name="uk_stripe_event_id", columnNames="provider_event_id"),
       indexes = {
           @Index(name="idx_event_payment", columnList="payment_id"),
           @Index(name="idx_event_type", columnList="event_type")
       })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StripePaymentEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="payment_id", nullable=false)
    private Payment payment;

    @Column(name="provider_event_id", length=120, nullable=false)
    private String providerEventId;

    @Column(name="event_type", length=80, nullable=false)
    private String eventType;

    @Column(name="signature_valid", nullable=false)
    private boolean signatureValid;

    @Lob
    @Column(name="raw_payload", nullable=false, columnDefinition = "LONGTEXT")
    private String rawPayload;

    @Column(nullable=false)
    private Instant receivedAt;

    private Instant processedAt;
}