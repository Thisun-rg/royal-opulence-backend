
package com.royalopulence.controller;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
<<<<<<< HEAD
import com.royalopulence.service.base.BookingService;

=======
import com.royalopulence.dto.booking.CancelBookingResponse;
import com.royalopulence.service.base.BookingService;
import jakarta.validation.Valid;
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

<<<<<<< HEAD
    @PostMapping
    public BookingResponse createBooking(@RequestBody BookingRequest request, Authentication authentication) {

        String userEmail = authentication.getName(); // Extract logged-in user
        String userId = userEmail;                  // Because Mongo IDs use email as unique ID

        return bookingService.createReservation(userId, request);
    }

    // ✅ FRONTEND: POST /api/bookings/checkout
    @PostMapping("/checkout")
    public BookingResponse checkout(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName(); // using email as identifier
        return bookingService.createReservation(userId, request);
    }

    // ✅ Optional: keep old endpoint too (POST /api/bookings)
    @PostMapping
    public BookingResponse createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return bookingService.createReservation(userId, request);
    }

    // ✅ CANCEL
    @PostMapping("/{reservationId}/cancel")
    public CancelBookingResponse cancelBooking(
            @PathVariable String reservationId,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        return bookingService.cancelReservation(reservationId, userId);
    }
}
