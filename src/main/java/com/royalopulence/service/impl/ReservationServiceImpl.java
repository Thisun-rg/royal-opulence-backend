package com.royalopulence.service.impl;

import com.royalopulence.dto.booking.ReservationRequest;
import com.royalopulence.dto.booking.ReservationResponse;
import com.royalopulence.model.core.Reservation;
import com.royalopulence.model.core.Room;
import com.royalopulence.model.operation.ReservationStatus;
import com.royalopulence.repository.ReservationRepository;
import com.royalopulence.repository.RoomRepository;
import com.royalopulence.service.base.ReservationService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    // ---------------- CREATE RESERVATION ----------------

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {

        if (!request.getCheckIn().isBefore(request.getCheckOut())) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        boolean conflict = reservationRepository.existsConflict(
                room.getId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        if (conflict) {
            throw new IllegalStateException("Room is already booked for selected dates");
        }

        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setUserId(request.getUserId());
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setGuests(request.getGuests());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());

        long nights = ChronoUnit.DAYS.between(
                request.getCheckIn(),
                request.getCheckOut()
        );

        BigDecimal totalPrice = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(Math.max(nights, 1)));

        reservation.setTotalPrice(totalPrice);

        Reservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    // ---------------- STATUS CHANGES ----------------

    @Override
    public ReservationResponse confirmReservation(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse checkIn(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse checkOut(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        return mapToResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse cancelReservation(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        reservation.setStatus(ReservationStatus.CANCELLED);
        return mapToResponse(reservationRepository.save(reservation));
    }

    // ---------------- LIST RESERVATIONS ----------------

    @Override
    public List<ReservationResponse> listReservations(
            Long roomId,
            String status,
            LocalDate from,
            LocalDate to
    ) {

        List<Reservation> reservations = (roomId != null)
                ? reservationRepository.findByRoomId(roomId)
                : reservationRepository.findAll();

        return reservations.stream()
                .filter(r -> status == null || r.getStatus().name().equalsIgnoreCase(status))
                .filter(r -> from == null || !r.getCheckOut().isBefore(from))
                .filter(r -> to == null || !r.getCheckIn().isAfter(to))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- AUTO CANCEL PENDING ----------------

    @Scheduled(fixedRate = 60000)
    public void autoCancelPending() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Reservation> expired = reservationRepository.findExpiredPending(threshold);

        expired.forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
        reservationRepository.saveAll(expired);
    }

    // ---------------- HELPERS ----------------

    private Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setRoomId(reservation.getRoom().getId());
        response.setRoomNumber(reservation.getRoom().getRoomNumber());
        response.setStatus(reservation.getStatus());
        response.setCheckIn(reservation.getCheckIn());
        response.setCheckOut(reservation.getCheckOut());
        response.setUserId(reservation.getUserId());
        response.setGuests(reservation.getGuests());
        response.setTotalPrice(reservation.getTotalPrice());
        response.setSpecialRequests(reservation.getSpecialRequests());
        return response;
    }
}
