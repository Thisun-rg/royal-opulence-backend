package com.royalopulence.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BookingResponse {

    private String reservationId;

    // Room info
    private String roomId;          // backward compatibility
    private List<String> roomIds;   // multiple rooms support
    private String roomNumber;
    private String roomTypeId;
    private String roomTypeName;

    // Dates
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // Booking details
    private int rooms;
    private int guests;

    private double totalAmount;
    private String status;

    // Payment
    private String paymentId;
    private String paymentStatus;

    // For Stripe / payment gateway
    private String clientSecret;
}
