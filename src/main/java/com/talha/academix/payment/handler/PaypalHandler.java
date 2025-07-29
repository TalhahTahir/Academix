package com.talha.academix.payment.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.Payee;
import com.paypal.orders.PurchaseUnitRequest;
import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.payment.PaymentHandler;
import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaypalHandler implements PaymentHandler {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode:sandbox}") // default to sandbox
    private String mode;

    private final PaymentService paymentService;

    private PayPalHttpClient getClient() {
        PayPalEnvironment env = "live".equalsIgnoreCase(mode)
            ? new PayPalEnvironment.Live(clientId, clientSecret)
            : new PayPalEnvironment.Sandbox(clientId, clientSecret);
        return new PayPalHttpClient(env);
    }

    @Override
    public boolean supports(PaymentRequest request) {
        return request.getMedium() == PaymentMedium.PAYPAL;
    }

    @Override
    public PaymentResponse initiate(PaymentRequest request) {
        try {
            PayPalHttpClient client = getClient();

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.checkoutPaymentIntent("CAPTURE");
            ApplicationContext applicationContext = new ApplicationContext()
                .brandName("Academix")
                .landingPage("LOGIN")
                .shippingPreference("NO_SHIPPING")
                .userAction("PAY_NOW");
            orderRequest.applicationContext(applicationContext);

            orderRequest.purchaseUnits(java.util.List.of(
                new PurchaseUnitRequest()
                    .amountWithBreakdown(new AmountWithBreakdown()
                        .currencyCode("USD")
                        .value(String.format("%.2f", request.getAmount() / 100.0))
                    )
                    .payee(new Payee().email(request.getAccount()))
            ));

            OrdersCreateRequest req = new OrdersCreateRequest();
            req.requestBody(orderRequest);

            Order order = client.execute(req).result();

            String approvalLink = order.links().stream()
                .filter(l -> "approve".equals(l.rel()))
                .findFirst()
                .map(LinkDescription::href)
                .orElse(null);

            String orderId = order.id();

 // After approval/capture, you would get payer info from the webhook/callback.
        // For demonstration, let's assume we have it here:
        String payerId = null;
        String brand = "PayPal";
        String accountReference = request.getAccount(); // Or a masked version

        // If this is the first time, save the payerId as token
        if (request.getToken() == null && payerId != null) {
            paymentService.saveTokenizedWallet(
                request.getWalletId(), // Pass userId/walletId as per your design
                PaymentMedium.PAYPAL,
                request.getAccount(),
                payerId,
                brand,
                accountReference
            );
        }

        return new PaymentResponse(
            false, // Not succeeded until captured
            approvalLink, // clientSecret is approval link
            order.status(),
            orderId,
            true // requiresAction, user must approve
        );

        } catch (Exception e) {
            return new PaymentResponse(false, null, e.getMessage(), null, false);
        }
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        // Webhook verification and processing logic.
        // You should verify the webhook using PayPal's API and then mark the payment as paid.
        // For brevity, pseudo-code:

        // Parse payload (JSON), extract resource.order_id, event_type, etc.
        // If event_type == "CHECKOUT.ORDER.APPROVED" or "PAYMENT.CAPTURE.COMPLETED":
        //    paymentService.markAsPaid(orderId);

        // Example (you may want to use Jackson for parsing):
        try {
            com.fasterxml.jackson.databind.JsonNode node = 
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);
            String eventType = node.get("event_type").asText();
            if ("CHECKOUT.ORDER.APPROVED".equals(eventType) || "PAYMENT.CAPTURE.COMPLETED".equals(eventType)) {
                String orderId = node.get("resource").get("id").asText();
                paymentService.markAsPaid(orderId); // You should store orderId in Payment.gatewayTransactionId
            }
        } catch (Exception e) {
            throw new RuntimeException("PayPal webhook processing failed", e);
        }
    }
}