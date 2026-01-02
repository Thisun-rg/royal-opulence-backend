package com.royalopulence.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
public class BookingRequest {

    private String userId;
    private String roomTypeId;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}


