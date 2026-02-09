package com.royalopulence.dto.booking;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private String reservationId;

    // keep for backward compat
    private String roomId;

    // ✅ new
    private List<String> roomIds;

    private String roomTypeId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private int rooms;
    private int guests;

    private double totalAmount;
    private String status;

    // ✅ payment
    private String paymentId;
    private String paymentStatus;
}
