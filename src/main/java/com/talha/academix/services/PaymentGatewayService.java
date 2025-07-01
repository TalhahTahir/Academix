package com.talha.academix.services;

import com.talha.academix.enums.PaymentMedium;

public interface  PaymentGatewayService {
    boolean charge (PaymentMedium medium, String account, Integer amount);
}
