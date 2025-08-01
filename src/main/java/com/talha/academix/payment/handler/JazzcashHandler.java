//real (Copilot AI)
package com.talha.academix.payment.handler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.payment.PaymentHandler;
import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JazzcashHandler implements PaymentHandler {

    @Value("${jazzcash.api.baseUrl}")
    private String baseUrl;

    @Value("${jazzcash.api.clientId}")
    private String clientId;

    @Value("${jazzcash.api.clientSecret}")
    private String clientSecret;

    @Value("${jazzcash.api.currency:PKR}")
    private String currency;

    private final PaymentService paymentService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(PaymentRequest request) {
        return request.getMedium() == PaymentMedium.JAZZCASH;
    }

    @Override
    public PaymentResponse initiate(PaymentRequest request) {
        try {
            // 1. Get access token (tokenization: save to Wallet if not yet present)
            String accessToken = request.getToken();
            if (accessToken == null || accessToken.isBlank()) {
                accessToken = authenticateAndGetToken();
                if (accessToken == null) {
                    return new PaymentResponse(false, null, "JazzCash authentication failed", null, false);
                }

                // Save token in Wallet (first time)
                paymentService.saveTokenizedWallet(
                    request.getWalletId(),
                    PaymentMedium.JAZZCASH,
                    request.getAccount(),
                    accessToken,
                    "JazzCash",
                    maskAccount(request.getAccount())
                );
            }

            // 2. Initiate payment API call
            PaymentResponse paymentResponse = initiatePayment(request, accessToken);

            return paymentResponse;

        } catch (Exception ex) {
            log.error("JazzCash payment error", ex);
            return new PaymentResponse(false, null, "Internal JazzCash processing error", null, false);
        }
    }

    private String authenticateAndGetToken() {
        String authUrl = baseUrl + "/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = restTemplate.exchange(authUrl, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.path("access_token").asText(null);
            } catch (Exception e) {
                log.error("Failed to parse JazzCash auth response", e);
            }
        }
        return null;
    }

    private PaymentResponse initiatePayment(PaymentRequest request, String accessToken) {
        String paymentUrl = baseUrl + "/payments/initiate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        String orderRefNum = "ORD-" + Instant.now().toEpochMilli();

        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", request.getAmount());
        payload.put("msisdn", request.getAccount());
        payload.put("orderRefNum", orderRefNum);
        payload.put("currency", currency);
        payload.put("description", "Payment via JazzCash");
        // Optionally: responseUrl, metadata, etc.

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            try {
                JsonNode respNode = objectMapper.readTree(response.getBody());

                String status = respNode.path("status").asText("");
                String txnId = respNode.path("transactionId").asText(null);
                String statusMsg = respNode.path("message").asText("No message");

                // You may want to persist extra metadata here as needed

                return new PaymentResponse(
                        "SUCCESS".equalsIgnoreCase(status),
                        null,
                        statusMsg,
                        txnId,
                        false
                );
            } catch (Exception e) {
                log.error("JazzCash response parsing failed", e);
                return new PaymentResponse(false, null, "Failed to parse JazzCash response", null, false);
            }
        }
        return new PaymentResponse(false, null, "JazzCash API error", null, false);
    }

    private String maskAccount(String account) {
        if (account == null || account.length() < 4) return "****";
        return "****" + account.substring(account.length() - 4);
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        // Webhook parsing: update Payment status by transactionId
        try {
            JsonNode node = objectMapper.readTree(payload);
            String txnId = node.path("transactionId").asText(null);
            String status = node.path("status").asText(null);

            if (txnId == null || status == null) {
                log.warn("Invalid JazzCash webhook payload (missing transactionId or status)");
                return;
            }

            if ("SUCCESS".equalsIgnoreCase(status)) {
                paymentService.markAsPaid(txnId);
                log.info("JazzCash payment marked as success for txnId={}", txnId);
            } else if ("FAILED".equalsIgnoreCase(status)) {
                // Implement markAsFailed in PaymentService if you want to track failures
                log.info("JazzCash payment marked as failed for txnId={}", txnId);
            } else {
                log.info("JazzCash webhook for txnId={} status={}", txnId, status);
            }
        } catch (Exception e) {
            log.error("Failed to process JazzCash webhook payload", e);
        }
    }
}
// a bit more practical (perplexity AI)
// package com.talha.academix.payment.handler;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.talha.academix.enums.PaymentMedium;
// import com.talha.academix.payment.PaymentHandler;
// import com.talha.academix.payment.model.PaymentRequest;
// import com.talha.academix.payment.model.PaymentResponse;
// import com.talha.academix.services.PaymentService;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.*;
// import org.springframework.stereotype.Component;
// import org.springframework.util.LinkedMultiValueMap;
// import org.springframework.util.MultiValueMap;
// import org.springframework.web.client.*;

// import java.time.Instant;
// import java.util.HashMap;
// import java.util.Map;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class JazzCashHandler implements PaymentHandler {

//     @Value("${jazzcash.api.baseUrl}")
//     private String baseUrl;

//     @Value("${jazzcash.api.clientId}")
//     private String clientId;

//     @Value("${jazzcash.api.clientSecret}")
//     private String clientSecret;

//     @Value("${jazzcash.api.currency:PKR}")
//     private String currency;

//     private final PaymentService paymentService;
//     private final RestTemplate restTemplate = new RestTemplate();
//     private final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public boolean supports(PaymentRequest request) {
//         return request.getMedium() == PaymentMedium.JAZZCASH;
//     }

//     @Override
//     public PaymentResponse initiate(PaymentRequest request) {
//         try {
//             // Step 1: Obtain access token if not present or expired
//             String accessToken = request.getToken();
//             if (accessToken == null || accessToken.isBlank() || !paymentService.isTokenValid(accessToken)) {
//                 accessToken = authenticateAndGetToken();
//                 if (accessToken == null) {
//                     return new PaymentResponse(false, "AUTH_FAILED", "Unable to authenticate with JazzCash", null, false);
//                 }

//                 // Save token for this wallet/user for reuse
//                 paymentService.saveTokenizedWallet(
//                     request.getWalletId(),
//                     PaymentMedium.JAZZCASH,
//                     request.getAccount(),
//                     accessToken,
//                     "JazzCash",
//                     maskAccount(request.getAccount())
//                 );
//             }

//             // Step 2: Initiate payment
//             PaymentResponse paymentResponse = initiatePayment(request, accessToken);

//             // Optional: after first payment, check if response includes a reusable token to save
//             // (Depends on JazzCash feature support for tokenization)
//             if (paymentResponse.isSuccess() && paymentResponse.getToken() != null) {
//                 paymentService.saveTokenizedWallet(
//                     request.getWalletId(),
//                     PaymentMedium.JAZZCASH,
//                     request.getAccount(),
//                     paymentResponse.getToken(),
//                     "JazzCash",
//                     maskAccount(request.getAccount())
//                 );
//             }

//             return paymentResponse;

//         } catch (HttpClientErrorException | HttpServerErrorException httpEx) {
//             log.error("HTTP error calling JazzCash API: status={}, response={}", httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
//             return new PaymentResponse(false, "HTTP_ERROR", "JazzCash API error: " + httpEx.getMessage(), null, false);
//         } catch (Exception ex) {
//             log.error("Unexpected error in JazzCash payment", ex);
//             return new PaymentResponse(false, "INTERNAL_ERROR", "Internal processing error", null, false);
//         }
//     }

//     private String authenticateAndGetToken() {
//         String authUrl = baseUrl + "/oauth/token";

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//         MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//         formData.add("client_id", clientId);
//         formData.add("client_secret", clientSecret);
//         formData.add("grant_type", "client_credentials");

//         HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

//         ResponseEntity<String> response = restTemplate.exchange(authUrl, HttpMethod.POST, entity, String.class);
//         if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//             try {
//                 JsonNode jsonNode = objectMapper.readTree(response.getBody());
//                 return jsonNode.path("access_token").asText(null);
//             } catch (Exception e) {
//                 log.error("Failed to parse authentication response", e);
//             }
//         }
//         return null;
//     }

//     private PaymentResponse initiatePayment(PaymentRequest request, String accessToken) {
//         String paymentUrl = baseUrl + "/payments/initiate";

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);
//         headers.setBearerAuth(accessToken);

//         // Construct unique order reference, could be your internal txn ID or UUID
//         String orderRefNum = "ORD-" + Instant.now().toEpochMilli();

//         Map<String, Object> payload = new HashMap<>();
//         payload.put("amount", request.getAmount());
//         payload.put("msisdn", request.getAccount());  // Here account is assumed mobile number for JazzCash
//         payload.put("orderRefNum", orderRefNum);
//         payload.put("currency", currency);
//         payload.put("description", request.getDescription() != null ? request.getDescription() : "Payment via JazzCash");
//         payload.put("responseUrl", request.getCallbackUrl());

//         if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
//             payload.put("metadata", request.getMetadata());
//         }

//         HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

//         ResponseEntity<String> response = restTemplate.exchange(paymentUrl, HttpMethod.POST, entity, String.class);
//         if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//             try {
//                 JsonNode respNode = objectMapper.readTree(response.getBody());

//                 String status = respNode.path("status").asText("");
//                 String txnId = respNode.path("transactionId").asText(null);
//                 String statusMsg = respNode.path("message").asText("No message");
//                 // Optional token if JazzCash returns something like that
//                 String paymentToken = respNode.path("paymentToken").asText(null);

//                 // Persist transaction metadata via paymentService
//                 paymentService.saveTransactionMetadata(txnId, request.getMetadata());

//                 return new PaymentResponse("SUCCESS".equalsIgnoreCase(status), null, statusMsg, txnId, false, paymentToken);

//             } catch (Exception e) {
//                 log.error("Failed to parse payment initiation response", e);
//                 return new PaymentResponse(false, "PARSE_ERROR", "Failed to parse JazzCash payment response", null, false);
//             }
//         } else {
//             return new PaymentResponse(false, "API_ERROR", "JazzCash API returned status " + response.getStatusCode(), null, false);
//         }
//     }

//     private String maskAccount(String account) {
//         if (account == null || account.length() < 4) return "****";
//         return "****" + account.substring(account.length() - 4);
//     }

//     @Override
//     public void handleWebhook(String payload, String signature) {
//         // Validate signature if JazzCash sends one (implementation depends on JazzCash spec)
//         // Parse payload and update payment status accordingly
//         try {
//             JsonNode node = objectMapper.readTree(payload);
//             String txnId = node.path("transactionId").asText(null);
//             String status = node.path("status").asText(null);

//             if (txnId == null || status == null) {
//                 log.warn("Invalid webhook payload: missing transactionId or status");
//                 return;
//             }

//             if ("SUCCESS".equalsIgnoreCase(status)) {
//                 paymentService.markAsPaid(txnId);
//                 log.info("Payment marked as success for txnId={}", txnId);
//             } else if ("FAILED".equalsIgnoreCase(status)) {
//                 paymentService.markAsFailed(txnId);
//                 log.info("Payment marked as failed for txnId={}", txnId);
//             } else {
//                 log.info("Webhook for txnId={} status={}", txnId, status);
//             }

//         } catch (Exception e) {
//             log.error("Failed to process JazzCash webhook payload", e);
//             throw new RuntimeException("JazzCash webhook processing failed", e);
//         }
//     }
// }


//little practical (copilot AI)
// package com.talha.academix.payment.handler;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;

// import com.talha.academix.enums.PaymentMedium;
// import com.talha.academix.payment.PaymentHandler;
// import com.talha.academix.payment.model.PaymentRequest;
// import com.talha.academix.payment.model.PaymentResponse;
// import com.talha.academix.services.PaymentService;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class JazzcashHandler implements PaymentHandler {

//     @Value("${jazzcash.api.baseUrl}")
//     private String baseUrl;
//     @Value("${jazzcash.api.clientId}")
//     private String clientId;
//     @Value("${jazzcash.api.clientSecret}")
//     private String clientSecret;

//     private final PaymentService paymentService;

//     @Override
//     public boolean supports(PaymentRequest request) {
//         return request.getMedium() == PaymentMedium.JAZZCASH;
//     }

//     @Override
//     public PaymentResponse initiate(PaymentRequest request) {
//         RestTemplate restTemplate = new RestTemplate();
//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);

//         String accessToken = request.getToken();

//         // First-time: get access token
//         if (accessToken == null || accessToken.isBlank()) {
//             // Authenticate with JazzCash API
//             String authUrl = baseUrl + "/oauth/token";
//             Map<String, String> authPayload = new HashMap<>();
//             authPayload.put("client_id", clientId);
//             authPayload.put("client_secret", clientSecret);
//             authPayload.put("grant_type", "client_credentials");

//             HttpEntity<Map<String, String>> authEntity = new HttpEntity<>(authPayload, headers);
//             ResponseEntity<Map> authResp = restTemplate.postForEntity(authUrl, authEntity, Map.class);

//             accessToken = (String) authResp.getBody().get("access_token");
//             // Tokenization: Save token to wallet for future
//             String brand = "JazzCash";
//             String accountReference = maskAccount(request.getAccount());
//             paymentService.saveTokenizedWallet(
//                 request.getWalletId(),
//                 PaymentMedium.JAZZCASH,
//                 request.getAccount(),
//                 accessToken,
//                 brand,
//                 accountReference
//             );
//         }

//         // Initiate payment
//         String payUrl = baseUrl + "/payments/initiate";
//         Map<String, Object> payPayload = new HashMap<>();
//         payPayload.put("access_token", accessToken);
//         payPayload.put("amount", request.getAmount());
//         payPayload.put("account", request.getAccount());

//         HttpEntity<Map<String, Object>> payEntity = new HttpEntity<>(payPayload, headers);
//         ResponseEntity<Map> payResp = restTemplate.postForEntity(payUrl, payEntity, Map.class);

//         boolean success = "SUCCESS".equalsIgnoreCase((String) payResp.getBody().get("status"));
//         String txnId = (String) payResp.getBody().get("transaction_id");
//         String statusMsg = (String) payResp.getBody().get("status_message");

//         return new PaymentResponse(success, null, statusMsg, txnId, false);
//     }

//     private String maskAccount(String account) {
//         if (account == null || account.length() < 4) return "****";
//         return "****" + account.substring(account.length() - 4);
//     }

//     @Override
//     public void handleWebhook(String payload, String signature) {
//         // If JazzCash provides webhooks, handle async status updates here.
//         try {
//             com.fasterxml.jackson.databind.JsonNode node =
//                 new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);
//             String txnId = node.get("transaction_id").asText();
//             String status = node.get("status").asText();
//             if ("SUCCESS".equalsIgnoreCase(status)) {
//                 paymentService.markAsPaid(txnId);
//             }
//         } catch (Exception e) {
//             throw new RuntimeException("JazzCash webhook processing failed", e);
//         }
//     }
// }

// simple
// package com.talha.academix.payment.handler;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;

// import com.talha.academix.enums.PaymentMedium;
// import com.talha.academix.payment.PaymentHandler;
// import com.talha.academix.payment.model.PaymentRequest;
// import com.talha.academix.payment.model.PaymentResponse;
// import com.talha.academix.services.PaymentService;

// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class JazzcashHandler implements PaymentHandler {

//     @Value("${jazzcash.api.baseUrl}")
//     private String baseUrl;
//     @Value("${jazzcash.api.clientId}")
//     private String clientId;
//     @Value("${jazzcash.api.clientSecret}")
//     private String clientSecret;

//     private final PaymentService paymentService;

//     @Override
//     public boolean supports(PaymentRequest request) {
//         return request.getMedium() == PaymentMedium.JAZZCASH;
//     }

//     @Override
//     public PaymentResponse initiate(PaymentRequest request) {
//         RestTemplate restTemplate = new RestTemplate();

//         // If token exists, use token for payment initiation
//         if (request.getToken() != null && !request.getToken().isBlank()) {
//             // Use token for direct payment (reusable)
//             String paymentUrl = baseUrl + "/payments";
//             Map<String, Object> payload = new HashMap<>();
//             payload.put("access_token", request.getToken());
//             payload.put("amount", request.getAmount());
//             payload.put("account", request.getAccount());

//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_JSON);

//             HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
//             ResponseEntity<Map> response = restTemplate.postForEntity(paymentUrl, entity, Map.class);

//             boolean success = "SUCCESS".equalsIgnoreCase((String) response.getBody().get("status"));
//             String txnId = (String) response.getBody().get("transaction_id");
//             String statusMsg = (String) response.getBody().get("status_message");

//             return new PaymentResponse(success, null, statusMsg, txnId, false);

//         } else {
//             // First time: authenticate/authorize and initiate payment
//             String authUrl = baseUrl + "/authorize";
//             Map<String, Object> authPayload = new HashMap<>();
//             authPayload.put("client_id", clientId);
//             authPayload.put("client_secret", clientSecret);
//             authPayload.put("account", request.getAccount()); // mobile number/IBAN

//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_JSON);

//             HttpEntity<Map<String, Object>> entity = new HttpEntity<>(authPayload, headers);
//             ResponseEntity<Map> authResponse = restTemplate.postForEntity(authUrl, entity, Map.class);

//             String accessToken = (String) authResponse.getBody().get("access_token");
//             String brand = "JazzCash";
//             String accountReference = "****" + request.getAccount().substring(request.getAccount().length() - 4);

//             // Save token in wallet table
//             paymentService.saveTokenizedWallet(
//                 request.getWalletId(),
//                 PaymentMedium.JAZZCASH,
//                 request.getAccount(),
//                 accessToken,
//                 brand,
//                 accountReference
//             );

//             // Now, initiate payment with token
//             String paymentUrl = baseUrl + "/payments";
//             Map<String, Object> payload = new HashMap<>();
//             payload.put("access_token", accessToken);
//             payload.put("amount", request.getAmount());
//             payload.put("account", request.getAccount());

//             HttpEntity<Map<String, Object>> paymentEntity = new HttpEntity<>(payload, headers);
//             ResponseEntity<Map> paymentResponse = restTemplate.postForEntity(paymentUrl, paymentEntity, Map.class);

//             boolean success = "SUCCESS".equalsIgnoreCase((String) paymentResponse.getBody().get("status"));
//             String txnId = (String) paymentResponse.getBody().get("transaction_id");
//             String statusMsg = (String) paymentResponse.getBody().get("status_message");

//             return new PaymentResponse(success, null, statusMsg, txnId, false);
//         }
//     }

//     @Override
//     public void handleWebhook(String payload, String signature) {
//         // If JazzCash provides webhooks, handle async status updates here.
//         // Parse payload, extract transaction_id, status, etc., and update payment.
//         // For brevity, here's a pseudo-code example:
//         try {
//             com.fasterxml.jackson.databind.JsonNode node = 
//                 new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);
//             String txnId = node.get("transaction_id").asText();
//             String status = node.get("status").asText();
//             if ("SUCCESS".equalsIgnoreCase(status)) {
//                 paymentService.markAsPaid(txnId);
//             }
//         } catch (Exception e) {
//             throw new RuntimeException("JazzCash webhook processing failed", e);
//         }
//     }
// }