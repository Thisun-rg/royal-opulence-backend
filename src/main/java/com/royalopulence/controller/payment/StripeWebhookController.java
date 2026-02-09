package com.royalopulence.controller.payment;

import com.royalopulence.model.core.Reservation;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.ReservationRepository;
import com.royalopulence.service.impl.PaymentServiceImpl;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentServiceImpl paymentServiceImpl;
    private final ReservationRepository reservationRepository;

    @Value("${stripe.webhookSecret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws Exception {

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Invalid signature");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (intent != null) {
                // 1) find payment by stripeIntentId
                Payment payment = paymentServiceImpl.findByStripeIntentIdOrThrow(intent.getId());

                // 2) mark payment SUCCESS
                payment.setStatus(PaymentStatus.PAID);
                paymentServiceImpl.save(payment);

                // 3) confirm reservation
                Reservation reservation = reservationRepository.findById(payment.getReservationId())
                        .orElseThrow(() -> new RuntimeException("Reservation not found"));

                reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
                reservation.setPaymentStatus(PaymentStatus.PAID.name());
                reservationRepository.save(reservation);
            }
        }

        return ResponseEntity.ok("OK");
    }
}
