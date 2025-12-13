package com.royalopulence.model.operation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String reservationId;

    // 🔹 Financial fields
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;

    private String currency;

    // 🔹 Payment lifecycle
    private String status;   // PENDING, SUCCESS, FAILED, REFUNDED
    private String method;   // STRIPE, PAYHERE, NOT_SET

    private String description;
    private Long createdAt;

    // 🔹 Stripe
    private String stripeIntentId;
}
