package com.talha.academix.services.impl;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Payout;
import com.stripe.model.Transfer;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.TransferCreateParams;
import com.talha.academix.services.StripeConnectPayoutService;

@Service
public class StripeConnectPayoutServiceImpl implements StripeConnectPayoutService {

    @Override
    public String createTransferToConnectedAccount(String destinationAccountId, long amountMinor, String currency)
            throws StripeException {

        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountMinor)
                .setCurrency(currency.toLowerCase())
                .setDestination(destinationAccountId)
                .build();

        Transfer transfer = Transfer.create(params);
        return transfer.getId();
    }

    @Override
    public String createPlatformPayout(long amountMinor, String currency) throws StripeException {
        PayoutCreateParams params = PayoutCreateParams.builder()
                .setAmount(amountMinor)
                .setCurrency(currency.toLowerCase())
                .build();

        Payout payout = Payout.create(params);
        return payout.getId();
    }
}
