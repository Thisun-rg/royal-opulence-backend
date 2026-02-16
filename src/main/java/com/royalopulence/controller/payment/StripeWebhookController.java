package com.royalopulence.controller.payment;

import com.royalopulence.model.core.Reservation;
import com.royalopulence.model.operation.Payment;
import com.royalopulence.model.utility.PaymentStatus;
import com.royalopulence.repository.PaymentRepository;
import com.royalopulence.repository.ReservationRepository;
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

    private final PaymentRepository paymentRepository;
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

                // 1️⃣ Find payment by Stripe Intent ID
                Payment payment = paymentRepository
                        .findByStripeIntentId(intent.getId())
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

                // 2️⃣ Mark payment as PAID
                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);

                // 3️⃣ Confirm reservation
                Reservation reservation = reservationRepository
                        .findById(payment.getReservationId())
                        .orElseThrow(() -> new RuntimeException("Reservation not found"));

                reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
                reservation.setPaymentStatus(PaymentStatus.PAID.name());
                reservationRepository.save(reservation);
            }
        }

        return ResponseEntity.ok("OK");
    }
}
