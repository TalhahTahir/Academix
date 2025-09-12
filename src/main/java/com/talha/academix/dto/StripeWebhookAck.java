package com.talha.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeWebhookAck {
    private String receivedEventId;
    private String outcome;
}