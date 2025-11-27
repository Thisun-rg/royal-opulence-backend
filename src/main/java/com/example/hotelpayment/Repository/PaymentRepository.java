package com.example.hotelpayment.Repository;

import com.example.hotelpayment.Model.Operation.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
}
