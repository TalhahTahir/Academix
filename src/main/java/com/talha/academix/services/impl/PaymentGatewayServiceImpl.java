package com.talha.academix.services.impl;

import org.springframework.stereotype.Service;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.services.PaymentGatewayService;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {
    
    @Override
    public boolean charge(PaymentMedium medium, String account, Integer amount) {
        // ✅ Simulate success for now
        // In real system, you'd call Stripe/Easypaisa/JazzCash APIs here
        return true;
        }
}
