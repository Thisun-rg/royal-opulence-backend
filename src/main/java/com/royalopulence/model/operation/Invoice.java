package com.royalopulence.model.operation;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "invoices")
public class Invoice {

    @Id
    private String id;

    private String reservationId;
    private String paymentId;
    private Double totalAmount;
    private String currency;

    private String invoiceNumber;
    private Long issuedAt;
}

