package com.talha.academix.services.impl;

import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;


import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.services.PaymentGatewayService;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    public PaymentGatewayServiceImpl() {
        Stripe.apiKey = "get Secret api key from stripe dashboard";
    }
    
    @Override
    public boolean charge(PaymentMedium medium, String account, Integer amount, PaymentType type) {
        switch (medium) {
            case PaymentMedium.STRIPE:
            ChargeCreateParams params = ChargeCreateParams.builder()
            .setAmount(Long.valueOf(amount * 100)) // Stripe expects amount in cents
            .setCurrency("usd")
            .setDescription("Course Payment")
            .setSource(account) // This should be a valid Stripe token or card ID
            .build();

            Charge charge = Charge.create(params);
            return charge.getPaid();



                
                break;

                case PaymentMedium.EASYPAYSA:
                
                break;

                case PaymentMedium.JAZZCASH:
                
                break;

                case PaymentMedium.BANK_ACCOUNT:
                
                break;
        
            default:
                break;
        }
        // ✅ Simulate success for now
        // In real system, you'd call Stripe/Easypaisa/JazzCash APIs here
        return true;
        }
}
