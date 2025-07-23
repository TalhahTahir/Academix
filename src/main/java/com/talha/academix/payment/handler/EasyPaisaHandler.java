package com.talha.academix.payment.handler;

import com.talha.academix.payment.PaymentHandler;
import com.talha.academix.payment.model.PaymentRequest;
import com.talha.academix.payment.model.PaymentResponse;

public class EasyPaisaHandler implements PaymentHandler {

    @Override
    public boolean supports(PaymentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'supports'");
    }

    @Override
    public PaymentResponse initiate(PaymentRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initiate'");
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleWebhook'");
    }

    
}
