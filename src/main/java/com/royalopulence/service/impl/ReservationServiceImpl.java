
package com.royalopulence.service.impl;

import com.royalopulence.dto.booking.ReservationRequest;
import com.royalopulence.dto.booking.ReservationResponse;
import com.royalopulence.model.core.Reservation;
import com.royalopulence.model.operation.ReservationStatus;
import com.royalopulence.model.core.Room;
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

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        boolean conflict = reservationRepository.existsConflict(
                room.getId(),
                request.getCheckIn(),
                request.getCheckOut(),
                ReservationStatus.PENDING,
                ReservationStatus.CONFIRMED,
                ReservationStatus.CHECKED_IN
        );

        if (conflict) {
            throw new IllegalStateException("Room already booked for selected dates");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalPrice = room.getPricePerNight()
                .multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setGuests(request.getGuests());
        reservation.setUserId(request.getUserId());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTotalPrice(totalPrice);

        reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    @Override
    public ReservationResponse confirmReservation(Long reservationId) {
        Reservation r = findByIdOrThrow(reservationId);
        r.setStatus(ReservationStatus.CONFIRMED);
        return mapToResponse(r);
    }

    @Override
    public ReservationResponse checkIn(Long reservationId) {
        Reservation r = findByIdOrThrow(reservationId);
        r.setStatus(ReservationStatus.CHECKED_IN);
        return mapToResponse(r);
    }

    @Override
    public ReservationResponse checkOut(Long reservationId) {
        Reservation r = findByIdOrThrow(reservationId);
        r.setStatus(ReservationStatus.CHECKED_OUT);
        return mapToResponse(r);
    }

    @Override
    public ReservationResponse cancelReservation(Long reservationId) {
        Reservation r = findByIdOrThrow(reservationId);
        r.setStatus(ReservationStatus.CANCELLED);
        return mapToResponse(r);
    }

    @Override
    public List<ReservationResponse> listReservations(
            Long roomId, String status, LocalDate from, LocalDate to) {

        List<Reservation> list = (roomId != null)
                ? reservationRepository.findByRoomId(roomId)
                : reservationRepository.findAll();

        return list.stream()
                .filter(r -> status == null || r.getStatus().name().equalsIgnoreCase(status))
                .filter(r -> from == null || !r.getCheckOut().isBefore(from))
                .filter(r -> to == null || !r.getCheckIn().isAfter(to))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Auto-cancel pending reservations older than 10 minutes
    @Scheduled(fixedRate = 60000)
    public void releasePendingReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Reservation> expired =
                reservationRepository.findExpiredPending(ReservationStatus.PENDING, threshold);

        expired.forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
        reservationRepository.saveAll(expired);
    }

    private Reservation findByIdOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    private ReservationResponse mapToResponse(Reservation r) {
        ReservationResponse resp = new ReservationResponse();
        resp.setId(r.getId());
        resp.setRoomId(r.getRoom().getId());
        resp.setRoomNumber(r.getRoom().getRoomNumber());
        resp.setStatus(r.getStatus());
        resp.setCheckIn(r.getCheckIn());
        resp.setCheckOut(r.getCheckOut());
        resp.setUserId(r.getUserId());
        resp.setGuests(r.getGuests());
        resp.setTotalPrice(r.getTotalPrice());
        resp.setSpecialRequests(r.getSpecialRequests());
        return resp;
    }
}
