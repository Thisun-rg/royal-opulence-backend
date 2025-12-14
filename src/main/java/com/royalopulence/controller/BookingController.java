
package com.royalopulence.controller;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
import com.royalopulence.service.base.BookingService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(@RequestBody BookingRequest request, Authentication authentication) {

        String userEmail = authentication.getName(); // Extract logged-in user
        String userId = userEmail;                  // Because Mongo IDs use email as unique ID

        return bookingService.createReservation(userId, request);
    }
}
