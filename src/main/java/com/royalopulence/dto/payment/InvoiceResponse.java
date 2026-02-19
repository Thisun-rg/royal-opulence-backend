package com.royalopulence.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceResponse {
    private String id;
    private String reservationId;
    private String paymentId;
    private Double totalAmount;
    private String currency;
    private String invoiceNumber;
    private Long issuedAt;
}

