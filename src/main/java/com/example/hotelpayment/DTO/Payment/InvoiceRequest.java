package com.example.hotelpayment.DTO.Payment;
import lombok.Data;

@Data
public class InvoiceRequest {
    private String reservationId;
    private String paymentId;
    private Double totalAmount;
}

