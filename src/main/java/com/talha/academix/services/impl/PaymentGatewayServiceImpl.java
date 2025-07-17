package com.talha.academix.services.impl;

import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.payment.model.PaymentResponse;
import com.talha.academix.services.PaymentGatewayService;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    public PaymentGatewayServiceImpl() {
        Stripe.apiKey = "get Secret api key from stripe dashboard";
    }

    @Override
    public PaymentResponse charge(PaymentMedium medium, String account, Integer amount, PaymentType type) {
        switch (medium) {
            case STRIPE:
                return chargeViaStripe(account, amount.intValue());
            case EASYPAYSA:
                // your EasyPaisa integration
            case JAZZCASH:
                // your JazzCash integration
            default:
                return new PaymentResponse(false, null, "Unsupported payment medium: " + medium, null);
        }
    }

    public PaymentResponse chargeViaStripe(String paymentMethodId, int amount) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(Long.valueOf(amount)) // in cents
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethodId)
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                    .setConfirm(true)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            String status = intent.getStatus();
            String clientSecret = intent.getClientSecret();
            String paymentIntentId = intent.getId();

            // Store intent id and status to your DB here

            if ("requires_action".equals(status)) {
                // Payment requires further user action
                return new PaymentResponse(false, clientSecret, "requires_action", paymentIntentId);
            } else if ("succeeded".equals(status)) {
                // Payment succeeded
                return new PaymentResponse(true, clientSecret, "succeeded", paymentIntentId);
            } else {
                // Other possible statuses you might handle
                return new PaymentResponse(false, clientSecret, status, paymentIntentId);
            }
        } catch (StripeException e) {
            // Log error properly
            return new PaymentResponse(false, null, e.getMessage(), null);
        }
    
    /*
     * @PostMapping("/webhook/stripe")
public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {
    String payload = IOUtils.toString(request.getReader());
    String sigHeader = request.getHeader("Stripe-Signature");

    try {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow();
            String intentId = intent.getId();
            
            // ✅ Mark your payment record as completed in DB
            paymentService.markAsPaid(intentId);
        }
    } catch (Exception e) {
        return ResponseEntity.status(400).body("Webhook error: " + e.getMessage());
    }
    return ResponseEntity.ok("");
}

     */
    }

}