package com.example.hotelpayment.Model.Operation;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String reservationId;
    private Double amount;
    private String currency;      // e.g. LKR, USD
    private String status;        // PENDING, SUCCESS, FAILED
    private String method;        // STRIPE, PAYHERE (later)
    private Long createdAt;       // timestamp (ms)
}

