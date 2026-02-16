package com.royalopulence.service.payment;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(double amount, String currency) throws Exception {
        long amountInCents = Math.round(amount * 100);

        PaymentIntentCreateParams params =
  PaymentIntentCreateParams.builder()
    .setAmount(amountInCents)
    .setCurrency(currency)
    .addPaymentMethodType("card")
    .build();


        return PaymentIntent.create(params);
    }
}
