package com.example.hotelpayment.DTO.Payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private String reservationId;
    private Double amount;
    private String currency;
}

