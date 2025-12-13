@Service
public class StripePaymentService {

    public StripePaymentService() {
        Stripe.apiKey = "sk_test_xxxxx";
    }

    public String createPaymentIntent(double amount) throws Exception {
        PaymentIntent intent = PaymentIntent.create(
            PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))
                .setCurrency("usd")
                .build()
        );
        return intent.getId();
    }
}
