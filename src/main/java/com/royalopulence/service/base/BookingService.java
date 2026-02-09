package com.royalopulence.service.base;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
import com.royalopulence.dto.booking.CancelBookingResponse;

public interface BookingService {
    BookingResponse createReservation(String userId, BookingRequest request);
    CancelBookingResponse cancelReservation(String reservationId, String userId);
}
