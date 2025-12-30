package com.talha.academix.services;

import com.stripe.exception.StripeException;

public interface StripeConnectPayoutService {

    /**
     * Teacher withdrawal: move money from Platform -> Teacher connected account.
     * @return Stripe transfer id (tr_...)
     */
    String createTransferToConnectedAccount(String destinationAccountId, long amountMinor, String currency) throws StripeException;

    /**
     * Admin withdrawal: payout money from Platform -> Platform bank.
     * @return Stripe payout id (po_...)
     */
    String createPlatformPayout(long amountMinor, String currency) throws StripeException;
}