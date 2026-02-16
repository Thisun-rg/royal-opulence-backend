
package com.royalopulence.controller;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
import com.royalopulence.dto.booking.CancelBookingResponse;
import com.royalopulence.service.base.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ✅ CREATE BOOKING  (POST /api/bookings)
    @PostMapping
    public BookingResponse createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName(); // email used as ID
        return bookingService.createReservation(userId, request);
    }

    // ✅ CHECKOUT (POST /api/bookings/checkout)
    @PostMapping("/checkout")
    public BookingResponse checkout(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return bookingService.createReservation(userId, request);
    }

    // ✅ CANCEL BOOKING (POST /api/bookings/{id}/cancel)
    @PostMapping("/{reservationId}/cancel")
    public CancelBookingResponse cancelBooking(
            @PathVariable String reservationId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return bookingService.cancelReservation(reservationId, userId);
    }
}
