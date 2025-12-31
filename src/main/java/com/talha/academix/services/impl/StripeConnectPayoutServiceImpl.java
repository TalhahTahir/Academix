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

/*
ok, so whats going on now is that:
INPUT:
POST: http://localhost:8081/api/withdrawals
JSON:
{
    "userId": 2,
    "amount": 50.00
}
---
OUTPUT:
ONboarding link
---
backendprocess : before clicking on link (before Onboarding  activation):
TeacherAccount entry created in DB with status: Ristrected.

after clicking on link and completing onboarding:

in stripe dashboard:
it is showing 2 entires in coonnected accouts:
1) restricted (created when we created TeacherAccount in DB, using email as account name)
2) Enabled (the one created with the OnBoardingUI)


AGAIN:
INPUT:
POST: http://localhost:8081/api/withdrawals
JSON:
{
    "userId": 2,
    "amount": 50.00
}
---
---
OUTPUT:
TeacherAccount entry updated in DB with status: completed.

WithDrawalDTO

withdrawal created in DB status= PROCESSING.

vault.pendingBalance created.
but transfer doesnt completed

2025-12-31T14:53:49.923+05:00  INFO 1684 --- [academix] [           main] com.talha.academix.config.DbInfoLogger   : Active profiles: 
2025-12-31T14:53:49.923+05:00  INFO 1684 --- [academix] [           main] com.talha.academix.config.DbInfoLogger   : DB JDBC URL: jdbc:mysql://localhost:3306/Acad
2025-12-31T14:53:49.923+05:00  INFO 1684 --- [academix] [           main] com.talha.academix.config.DbInfoLogger   : DB JDBC USER: root@localhost
2025-12-31T14:53:49.931+05:00  INFO 1684 --- [academix] [           main] com.talha.academix.config.DbInfoLogger   : DB SQL DATABASE(): academixdb
2025-12-31T14:54:35.548+05:00  INFO 1684 --- [academix] [nio-8081-exec-2] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispat
2025-12-31T14:54:35.548+05:00  INFO 1684 --- [academix] [nio-8081-exec-2] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-12-31T14:54:35.556+05:00  INFO 1684 --- [academix] [nio-8081-exec-2] o.s.web.servlet.DispatcherServlet        : Completed initialization in 8 ms
2025-12-31T14:54:40.276+05:00  INFO 1684 --- [academix] [nio-8081-exec-2] c.t.a.s.impl.TeacherAccountServiceImpl   : TeacherAccount persisted: teacherId=2, acctIdqv4JE1
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:55:42.303+05:00  INFO 1684 --- [academix] [nio-8081-exec-4] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkM81Qs3fqv4JE19
account.updated
2025-12-31T14:55:42.648+05:00  INFO 1684 --- [academix] [nio-8081-exec-4] c.t.a.s.i.StripePaymentEventServiceImpl  : Received account event: account.updated for es3fqv4JE19LJPEa7x
1.3 --- Stripe Web Controller executed
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:57:32.442+05:00  INFO 1684 --- [academix] [nio-8081-exec-6] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkM9nQs3fqv4JE1j
account.updated
2025-12-31T14:57:32.458+05:00  INFO 1684 --- [academix] [nio-8081-exec-6] c.t.a.s.i.StripePaymentEventServiceImpl  : Received account event: account.updated for es3fqv4JE1j2KBe49y
1.3 --- Stripe Web Controller executed
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:57:32.536+05:00  INFO 1684 --- [academix] [nio-8081-exec-7] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkM9nQs3fqv4JE1h
capability.updated
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:57:32.536+05:00  INFO 1684 --- [academix] [nio-8081-exec-8] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkM9oQs3fqv4JE1n
person.updated
2025-12-31T14:57:32.592+05:00  WARN 1684 --- [academix] [nio-8081-exec-7] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt1hHLRFi9K
2025-12-31T14:57:32.592+05:00  WARN 1684 --- [academix] [nio-8081-exec-8] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt1nsoc9HjH
1.3 --- Stripe Web Controller executed
1.3 --- Stripe Web Controller executed
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:58:06.324+05:00  INFO 1684 --- [academix] [nio-8081-exec-9] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMALQs3fqv4JE1G
account.updated
2025-12-31T14:58:06.347+05:00  INFO 1684 --- [academix] [nio-8081-exec-9] c.t.a.s.i.StripePaymentEventServiceImpl  : Received account event: account.updated for es3fqv4JE1Gw4uokeE
1.3 --- Stripe Web Controller executed
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:58:06.410+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMALQs3fqv4JE14
account.external_account.created
2025-12-31T14:58:06.426+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Received account event: account.external_acco
event evt_1SkMALQs3fqv4JE14pnGvkt7
1.3 --- Stripe Web Controller executed
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:59:28.673+05:00  INFO 1684 --- [academix] [nio-8081-exec-7] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMBgQuS0PFQJTWR
transfer.created
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
2025-12-31T14:59:28.853+05:00  INFO 1684 --- [academix] [nio-8081-exec-9] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMBgQs3fqv4JE1V
payment.created
1.3 --- Stripe Web Controller executed
2025-12-31T14:59:29.080+05:00  WARN 1684 --- [academix] [nio-8081-exec-9] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt1V8bUwYNQ
1.3 --- Stripe Web Controller executed
2025-12-31T14:59:28.853+05:00  INFO 1684 --- [academix] [nio-8081-exec-9] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMBgQs3fqv4JE1V
payment.created
1.3 --- Stripe Web Controller executed
2025-12-31T14:59:29.080+05:00  WARN 1684 --- [academix] [nio-8081-exec-9] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt1V8bUwYNQ
1.3 --- Stripe Web Controller executed
1.3 --- Stripe Web Controller executed
2025-12-31T14:59:29.080+05:00  WARN 1684 --- [academix] [nio-8081-exec-9] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt1V8bUwYNQ
1.3 --- Stripe Web Controller executed
1V8bUwYNQ
1.3 --- Stripe Web Controller executed
1.3 --- Stripe Web Controller executed
1.1 --- Received Stripe webhook
1.1 --- Received Stripe webhook
1.2 --- Stripe Web Controller running
1.2 --- Stripe Web Controller running
2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN6wEVY of typele
2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN6wEVY-31T15:00:29.211+05:00  WARN 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN6wE12-31T15:00:29.211+05:00  WARN 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN6-12-31T15:00:29.211+05:00  WARN 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN5-12-31T15:00:29.211+05:00  WARN 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event ev2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1Z025-12-31T15:00:29.211+05:00  WARN 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event ev2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN6wEVY of type yplance.available
2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN6wEVY of tv4JEy025-12-31T15:00:29.211+05:00  WARN 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt_1SkMCeQs3fq2025-12-31T15:00:29.187+05:00  INFO 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Processing Stripe event evt_1SkMCeQs3fqv4JE1ZYN6wEVY of type balance.available
2025-12-31T15:00:29.211+05:00  WARN 1684 --- [academix] [io-8081-exec-10] c.t.a.s.i.StripePaymentEventServiceImpl  : Could not resolve PaymentIntent for event evt_1SkMCeQs3fqv4JE1ZYN6wEVY
1.3 --- Stripe Web Controller executed

it is saying Could not resolve PaymentIntent for event evt_1SkMCeQs3fqv4JE1ZYN6wEVY, is it a problem?
*/