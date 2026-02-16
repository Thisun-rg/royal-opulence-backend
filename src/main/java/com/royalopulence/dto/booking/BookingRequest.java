package com.royalopulence.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BookingRequest {


    private String userId;
    private String roomTypeId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    @NotNull
    private String roomTypeId;

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    @Min(1)
    @Max(10)
    private int rooms = 1;

    // total guests for the booking (NOT per-room), but we validate using max 2/room
    @Min(1)
    private int guests = 1;

}
