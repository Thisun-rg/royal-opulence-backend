package com.royalopulence.service.base;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
<<<<<<< HEAD

public interface BookingService {
    BookingResponse createReservation(String userId, BookingRequest request);
=======
import com.royalopulence.dto.booking.CancelBookingResponse;

public interface BookingService {
    BookingResponse createReservation(String userId, BookingRequest request);
    CancelBookingResponse cancelReservation(String reservationId, String userId);
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
}
