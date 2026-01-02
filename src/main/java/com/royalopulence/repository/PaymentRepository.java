package com.royalopulence.repository;

import com.royalopulence.model.operation.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByReservationId(String reservationId);
}
