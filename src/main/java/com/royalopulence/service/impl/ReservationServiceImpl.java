
package com.royalopulence.service.impl;

import com.royalopulence.dto.booking.ReservationRequest;
import com.royalopulence.dto.booking.ReservationResponse;
import com.royalopulence.model.operation.*;
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

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {

        boolean conflict = reservationRepository.existsConflict(
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut()
        );

        if (conflict) {
            throw new IllegalStateException("Room is not available");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());

        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setGuests(request.getGuests());
        reservation.setUserId(request.getUserId());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setTotalPrice(room.getPricePerNight().multiply(BigDecimal.valueOf(nights)));

        return mapToResponse(reservationRepository.save(reservation));
    }

    @Override
    public List<ReservationResponse> listReservations(Long roomId, String status, LocalDate from, LocalDate to) {

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

    @Scheduled(fixedRate = 60000)
    public void releasePendingReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        reservationRepository.findExpiredPending(threshold)
                .forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
    }

    private ReservationResponse mapToResponse(Reservation r) {
        ReservationResponse res = new ReservationResponse();
        res.setId(r.getId());
        res.setRoomId(r.getRoom().getId());
        res.setRoomNumber(r.getRoom().getRoomNumber());
        res.setStatus(r.getStatus());
        res.setCheckIn(r.getCheckIn());
        res.setCheckOut(r.getCheckOut());
        res.setGuests(r.getGuests());
        res.setUserId(r.getUserId());
        res.setTotalPrice(r.getTotalPrice());
        res.setSpecialRequests(r.getSpecialRequests());
        return res;
    }
}
