package com.talha.academix.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Central place for Stripe SDK initialization and factory methods for
 * PaymentIntent creation (initiation chores).
 */
@Configuration
@Slf4j
public class StripeConfig {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.default-currency:usd}")
    private String defaultCurrency;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        log.info("Stripe initialized with default currency {}", defaultCurrency);
    }

    /**
     * Create a PaymentIntent with automatic payment methods enabled.
     *
     * @param amountMinor amount in the smallest currency unit (e.g. cents)
     * @param currency    ISO currency (if null uses default)
     * @param metadata    additional metadata (must not be null if you rely on it downstream)
     */
    public PaymentIntent createPaymentIntent(long amountMinor,
                                             String currency,
                                             Map<String, String> metadata) throws StripeException {

        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder()
                .setAmount(amountMinor)
                .setCurrency(currency == null ? defaultCurrency : currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build());

        if (metadata != null) {
            metadata.forEach(builder::putMetadata);
        }

        PaymentIntent intent = PaymentIntent.create(builder.build());
        log.debug("Created PaymentIntent {}", intent.getId());
        return intent;
    }
}