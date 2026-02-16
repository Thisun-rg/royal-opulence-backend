package com.royalopulence.dto.booking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    private String userId;

    @NotNull
    private String roomTypeId;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    @Min(1)
    @Max(10)
    private int rooms = 1;

    // Total guests for the booking (NOT per-room)
    @Min(1)
    private int guests = 1;
}
