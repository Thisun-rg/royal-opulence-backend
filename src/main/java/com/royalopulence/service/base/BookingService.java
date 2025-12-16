package com.royalopulence.service.base;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;

public interface BookingService {
    BookingResponse createReservation(String userId, BookingRequest request);
}
