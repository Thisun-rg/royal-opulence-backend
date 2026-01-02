package com.royalopulence.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {

    private String paymentId;
    private String reservationId;

    // 🔹 Clear financial breakdown
    private Double baseAmount;
    private Double taxAmount;
    private Double totalAmount;

    private String currency;
    private String status;        // PENDING, SUCCESS, FAILED
    private String method;        // NOT_SET, STRIPE
    private Long createdAt;
}
