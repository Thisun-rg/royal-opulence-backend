package com.royalopulence.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String reservationId;
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;
    private String currency;
    private String status;
    private String method;
    private Long createdAt;

    // ✅ Stripe
    private String clientSecret;
}
