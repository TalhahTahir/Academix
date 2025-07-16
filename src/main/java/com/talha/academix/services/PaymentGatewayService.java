package com.talha.academix.services;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.model.PaymentResponse;

public interface  PaymentGatewayService {
    PaymentResponse charge (PaymentMedium medium, String account, Integer amount, PaymentType type);
}
