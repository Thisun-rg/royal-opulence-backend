package com.royalopulence.repository;

import com.royalopulence.model.operation.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
}

