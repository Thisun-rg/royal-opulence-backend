package com.royalopulence.model.operation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.royalopulence.model.utility.PaymentMethod;
import com.royalopulence.model.utility.PaymentStatus;

import lombok.Data;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String reservationId;

    // Financials
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String currency;

    // Lifecycle (ENUMS – Mongo handles as String automatically)
    private PaymentStatus status;
    private PaymentMethod method;

    private String description;

    private Long createdAt;
    private Long expiresAt;

    // Stripe
    private String stripeIntentId;

    // Hotel-grade accounting (optional, future-ready)
    private Double exchangeRate;
    private Double amountLkr;

    private Double refundAmount;
    private Long refundedAt;

    private String stripeClientSecret; // store so you can return it easily if needed

}
