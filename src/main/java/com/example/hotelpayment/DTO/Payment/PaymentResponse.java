package com.example.hotelpayment.DTO.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String reservationId;
    private Double amount;
    private String currency;
    private String status;        // PENDING, SUCCESS, FAILED
    private String method;        // NOT_SET, STRIPE, PAYHERE (later)
    private Long createdAt;       // timestamp (ms)
}


