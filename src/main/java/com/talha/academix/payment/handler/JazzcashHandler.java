package com.talha.academix.payment.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.payment.PaymentHandler;
import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JazzcashHandler implements PaymentHandler {

    @Value("${jazzcash.api.baseUrl}")
    private String baseUrl;
    @Value("${jazzcash.api.clientId}")
    private String clientId;
    @Value("${jazzcash.api.clientSecret}")
    private String clientSecret;

    private final PaymentService paymentService;

    @Override
    public boolean supports(PaymentRequest request) {
        return request.getMedium() == PaymentMedium.JAZZCASH;
    }

    @Override
    public PaymentResponse initiate(PaymentRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        // If token exists, use token for payment initiation
        if (request.getToken() != null && !request.getToken().isBlank()) {
            // Use token for direct payment (reusable)
            String paymentUrl = baseUrl + "/payments";
            Map<String, Object> payload = new HashMap<>();
            payload.put("access_token", request.getToken());
            payload.put("amount", request.getAmount());
            payload.put("account", request.getAccount());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(paymentUrl, entity, Map.class);

            boolean success = "SUCCESS".equalsIgnoreCase((String) response.getBody().get("status"));
            String txnId = (String) response.getBody().get("transaction_id");
            String statusMsg = (String) response.getBody().get("status_message");

            return new PaymentResponse(success, null, statusMsg, txnId, false);

        } else {
            // First time: authenticate/authorize and initiate payment
            String authUrl = baseUrl + "/authorize";
            Map<String, Object> authPayload = new HashMap<>();
            authPayload.put("client_id", clientId);
            authPayload.put("client_secret", clientSecret);
            authPayload.put("account", request.getAccount()); // mobile number/IBAN

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(authPayload, headers);
            ResponseEntity<Map> authResponse = restTemplate.postForEntity(authUrl, entity, Map.class);

            String accessToken = (String) authResponse.getBody().get("access_token");
            String brand = "JazzCash";
            String accountReference = "****" + request.getAccount().substring(request.getAccount().length() - 4);

            // Save token in wallet table
            paymentService.saveTokenizedWallet(
                request.getWalletId(),
                PaymentMedium.JAZZCASH,
                request.getAccount(),
                accessToken,
                brand,
                accountReference
            );

            // Now, initiate payment with token
            String paymentUrl = baseUrl + "/payments";
            Map<String, Object> payload = new HashMap<>();
            payload.put("access_token", accessToken);
            payload.put("amount", request.getAmount());
            payload.put("account", request.getAccount());

            HttpEntity<Map<String, Object>> paymentEntity = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> paymentResponse = restTemplate.postForEntity(paymentUrl, paymentEntity, Map.class);

            boolean success = "SUCCESS".equalsIgnoreCase((String) paymentResponse.getBody().get("status"));
            String txnId = (String) paymentResponse.getBody().get("transaction_id");
            String statusMsg = (String) paymentResponse.getBody().get("status_message");

            return new PaymentResponse(success, null, statusMsg, txnId, false);
        }
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        // If JazzCash provides webhooks, handle async status updates here.
        // Parse payload, extract transaction_id, status, etc., and update payment.
        // For brevity, here's a pseudo-code example:
        try {
            com.fasterxml.jackson.databind.JsonNode node = 
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);
            String txnId = node.get("transaction_id").asText();
            String status = node.get("status").asText();
            if ("SUCCESS".equalsIgnoreCase(status)) {
                paymentService.markAsPaid(txnId);
            }
        } catch (Exception e) {
            throw new RuntimeException("JazzCash webhook processing failed", e);
        }
    }
}