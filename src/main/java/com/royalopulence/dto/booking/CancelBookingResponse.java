package com.royalopulence.dto.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelBookingResponse {
    private String reservationId;
    private String previousStatus;
    private String newStatus;

    private String paymentId;
    private String paymentStatus;

    private double totalAmount;
    private double refundAmount;
    private double refundPercentage; // 1.0, 0.5, 0.0

    private String message;
}
