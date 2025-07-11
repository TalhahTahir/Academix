package com.talha.academix.services;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;

public interface  PaymentGatewayService {
    boolean charge (PaymentMedium medium, String account, Integer amount, PaymentType type);
}
