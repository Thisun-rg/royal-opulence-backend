package com.royalopulence.service.payment;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    public StripePaymentService() {
        // 🔴 Test key only (NO REAL MONEY)
        Stripe.apiKey = "sk_test_REPLACE_WITH_TEST_KEY";
    }

    public PaymentIntent createPaymentIntent(double amount, String currency) throws Exception {

        PaymentIntentCreateParams params
                = PaymentIntentCreateParams.builder()
                        .setAmount((long) (amount * 100)) // cents
                        .setCurrency(currency.toLowerCase())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}
