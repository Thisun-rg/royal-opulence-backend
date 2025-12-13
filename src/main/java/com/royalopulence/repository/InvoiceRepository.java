package com.royalopulence.repository;

import com.royalopulence.model.operation.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {

    // Get invoice by Payment ID
    Optional<Invoice> findByPaymentId(String paymentId);

    // Get all invoices by Reservation ID
    List<Invoice> findByReservationId(String reservationId);
}
