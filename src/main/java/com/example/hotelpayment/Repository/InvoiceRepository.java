package com.example.hotelpayment.Repository;

import com.example.hotelpayment.Model.Operation.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
}
