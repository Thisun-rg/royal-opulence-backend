package com.royalopulence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalopulence.model.operation.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    // list all payments of a reservation
    List<Payment> findByReservationId(String reservationId);

    // latest payment for refund logic
    Optional<Payment> findTopByReservationIdOrderByCreatedAtDesc(String reservationId);

    // webhook support
    Optional<Payment> findByStripeIntentId(String stripeIntentId);
}
