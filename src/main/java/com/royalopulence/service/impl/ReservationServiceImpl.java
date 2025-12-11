package com.royalopulence.service.impl;


import com.royalopulence.dto.booking.ReservationRequest;
import com.royalopulence.dto.booking.ReservationResponse;
import com.royalopulence.model.operation.Reservation;
import com.royalopulence.model.operation.ReservationStatus;
import com.royalopulence.model.operation.Room;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
return mapToResponse(r);
}


@Override
public List<ReservationResponse> listReservations(Long roomId, String status, LocalDate from, LocalDate to) {
List<Reservation> list;
if (roomId != null) {
list = reservationRepository.findByRoomId(roomId);
} else {
list = reservationRepository.findAll();
}
// basic filtering by status and date range
return list.stream()
.filter(r -> status == null || r.getStatus().name().equalsIgnoreCase(status))
.filter(r -> (from == null || !r.getCheckOut().isBefore(from)))
.filter(r -> (to == null || !r.getCheckIn().isAfter(to)))
.map(this::mapToResponse)
.collect(Collectors.toList());
}


// Scheduled task to cancel pending reservations older than 10 minutes
@Scheduled(fixedRate = 60000)
@Transactional
public void releasePendingReservations() {
LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
List<Reservation> expired = reservationRepository.findExpiredPending(threshold);
if (!expired.isEmpty()) {
expired.forEach(r -> r.setStatus(ReservationStatus.CANCELLED));
reservationRepository.saveAll(expired);
}
}


private Reservation findByIdOrThrow(Long id) {
return reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
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
