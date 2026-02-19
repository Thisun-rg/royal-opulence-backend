package com.royalopulence.dto.booking;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private String reservationId;

    private String roomId; // backward compat
    private List<String> roomIds;

    private String roomTypeId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private int rooms;
    private int guests;

    private double totalAmount;
    private String status;

    private String paymentId;
    private String paymentStatus;

    // ✅ ADD THIS
    private String clientSecret;
}
