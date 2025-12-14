package com.royalopulence.service.payment;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    public StripePaymentService(
            @Value("${stripe.secret.key}") String secretKey
    ) {
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(double amount, String currency) throws Exception {

        PaymentIntentCreateParams params
                = PaymentIntentCreateParams.builder()
                        .setAmount((long) (amount * 100)) // convert to cents
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
