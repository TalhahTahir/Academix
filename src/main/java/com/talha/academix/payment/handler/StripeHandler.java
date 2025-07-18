package com.talha.academix.payment.handler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.payment.PaymentHandler;
import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StripeHandler implements PaymentHandler {

    @Value("${stripe.api.key}")
    private String stripeKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final PaymentService paymentService;

    @Override
    public boolean supports(PaymentRequest req) {
        return req.getMedium() == PaymentMedium.STRIPE;
    }

    @Override
    public PaymentResponse initiate(PaymentRequest req) {
        Stripe.apiKey = stripeKey;
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(req.getAmount())
                    .setCurrency("usd")
                    .setPaymentMethod(req.getAccount())
                    .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL)
                    .setConfirm(true)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            String status = intent.getStatus();
            String clientSecret = intent.getClientSecret();
            String id = intent.getId();
            boolean success = "succeeded".equals(status);
            boolean requiresAction = "requires_action".equals(status);

            return new PaymentResponse(
                    success,
                    clientSecret,
                    status,
                    id,
                    requiresAction);
        } catch (StripeException e) {
            return new PaymentResponse(false, null, e.getMessage(), null, false);
        }
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, signature, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid Stripe webhook signature", e);
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event
                    .getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new RuntimeException("Cannot deserialize Stripe event"));

            paymentService.markAsPaid(intent.getId());
        }
    }
}
