
package com.royalopulence.service.base;

import com.royalopulence.dto.booking.ReservationRequest;
import com.royalopulence.dto.booking.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse confirmReservation(Long reservationId);
    ReservationResponse checkIn(Long reservationId);
    ReservationResponse checkOut(Long reservationId);
    ReservationResponse cancelReservation(Long reservationId);
    List<ReservationResponse> listReservations(Long roomId, String status, LocalDate from, LocalDate to);
}
