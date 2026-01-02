package com.royalopulence.dto.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {

    private String reservationId;
    private String roomId;
    private String roomNumber;
    private String roomTypeName;
    private String status;
    private double totalAmount;
}
