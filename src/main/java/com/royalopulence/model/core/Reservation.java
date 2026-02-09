package com.royalopulence.model.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;

    private String userId;

    // ✅ Multiple rooms reserved under one booking
    private List<String> roomIds;

    private String roomTypeId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // ✅ booking inputs
    private int rooms;
    private int guests;

    // ✅ financial
    private double totalAmount;

    // ✅ reservation lifecycle
    private ReservationStatus status;

    // payment linking (optional)
    private String paymentId;
    private String paymentStatus;

    // cancellation
    private String cancellationReason;
    private long createdAt;
    private LocalDateTime cancelledAt;

    public enum ReservationStatus {
        PENDING_PAYMENT,
        CONFIRMED,
        CANCELLED
    }
}

