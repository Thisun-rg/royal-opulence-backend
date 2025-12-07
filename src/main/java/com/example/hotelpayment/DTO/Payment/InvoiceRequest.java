package com.example.hotelpayment.DTO.Payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InvoiceRequest {

    @NotBlank(message = "Reservation ID is required")
    private String reservationId;

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    private String currency; // optional, can mirror payment currency
}


