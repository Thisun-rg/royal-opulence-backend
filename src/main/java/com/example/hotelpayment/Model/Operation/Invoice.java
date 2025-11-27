package com.example.hotelpayment.Model.Operation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "invoices")
public class Invoice {

    @Id
    private String id;

    private String reservationId;
    private String paymentId;
    private Double totalAmount;

    private String invoiceNumber; // new
    private String createdAt;     // new
}
