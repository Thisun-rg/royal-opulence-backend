package com.royalopulence.service.payment;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    public StripePaymentService() {
        Stripe.apiKey = "sk_test_REPLACE_WITH_TEST_KEY";
    }

    public String createPaymentIntent(double amount) throws Exception {
        PaymentIntent intent = PaymentIntent.create(
                PaymentIntentCreateParams.builder()
                        .setAmount((long) (amount * 100)) // cents
                        .setCurrency("usd")
                        .build()
        );
        return intent.getId();
    }
}
