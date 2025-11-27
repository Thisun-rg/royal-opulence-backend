package com.example.hotelpayment.DTO.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceResponse {
    private String id;
    private String reservationId;
    private String paymentId;
    private Double totalAmount;
    private String invoiceNumber;
    private String createdAt;
}

