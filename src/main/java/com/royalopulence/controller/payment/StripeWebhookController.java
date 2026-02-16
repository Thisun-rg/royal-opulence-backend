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
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader
    ) {
        Event event;

        // 1️⃣ Verify Stripe signature
        try {
            if (sigHeader == null) {
                return ResponseEntity.badRequest().body("Missing Stripe-Signature header");
            }
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Invalid Stripe signature");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Webhook error: " + e.getMessage());
        }

        // 2️⃣ Handle payment success
        if ("payment_intent.succeeded".equals(event.getType())) {

            PaymentIntent intent = (PaymentIntent) event
                    .getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (intent == null) {
                return ResponseEntity.ok("Ignored (no intent)");
            }

            // Find payment by Stripe Intent ID
            Payment payment = paymentServiceImpl
                    .findByStripeIntentIdOrThrow(intent.getId());

            // Mark payment as PAID
            payment.setStatus(PaymentStatus.PAID);
            paymentServiceImpl.save(payment);

            // Confirm reservation
            Reservation reservation = reservationRepository
                    .findById(payment.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));

            reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
            reservation.setPaymentStatus(PaymentStatus.PAID.name());
            reservationRepository.save(reservation);
        }

        return ResponseEntity.ok("OK");
    }
}
