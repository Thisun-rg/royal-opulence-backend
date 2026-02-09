package com.royalopulence.controller;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
import com.royalopulence.dto.booking.CancelBookingResponse;
import com.royalopulence.service.base.BookingService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ✅ CREATE BOOKING
    @PostMapping
    public BookingResponse createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName(); // logged-in user
        String userId = userEmail;                   // email used as Mongo identifier

        return bookingService.createReservation(userId, request);
    }

    // ✅ CANCEL BOOKING (NEW)
    @PostMapping("/{reservationId}/cancel")
    public CancelBookingResponse cancelBooking(
            @PathVariable String reservationId,
            Authentication authentication) {

        String userEmail = authentication.getName();
        String userId = userEmail;

        return bookingService.cancelReservation(reservationId, userId);
    }
}

