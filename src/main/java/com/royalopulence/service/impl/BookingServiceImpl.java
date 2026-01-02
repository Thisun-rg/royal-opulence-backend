package com.royalopulence.service.impl;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
import com.royalopulence.model.core.*;
import com.royalopulence.repository.*;
import com.royalopulence.service.base.BookingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public BookingResponse createReservation(String userId, BookingRequest request) {

        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("Room type not found"));

        // 🔍 STEP 1 — Find all rooms of the selected room type
        List<Room> rooms = roomRepository.findByRoomTypeId(roomType.getId());

        // 🔎 STEP 2 — Find the first room that is available for the given dates
        Room availableRoom = rooms.stream()
                .filter(room -> isRoomAvailable(
                        room.getId(), 
                        request.getCheckInDate(), 
                        request.getCheckOutDate()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No rooms available for selected dates"));

        // 💰 STEP 3 — Calculate total price
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double totalAmount = roomType.getPricePerNight() * nights;

        // 📝 STEP 4 — Create reservation
        Reservation reservation = Reservation.builder()
        .userId(request.getUserId())
        .roomId(availableRoom.getId())
        .checkInDate(request.getCheckInDate())
        .checkOutDate(request.getCheckOutDate())
        .status("BOOKED")
        .build();

        Reservation saved = reservationRepository.save(reservation);


        // 🚪 STEP 5 — Mark room as reserved
        availableRoom.setStatus("RESERVED");
        roomRepository.save(availableRoom);

        return BookingResponse.builder()
                .reservationId(reservation.getId())
                .roomId(availableRoom.getId())
                .roomNumber(availableRoom.getRoomNumber())
                .roomTypeName(roomType.getName())
                .totalAmount(totalAmount)
                .status("CONFIRMED")
                .build();
    }

    private boolean isRoomAvailable(String roomId, java.time.LocalDate checkIn, java.time.LocalDate checkOut) {
        List<Reservation> reservations = reservationRepository.findByRoomId(roomId);

        return reservations.stream().noneMatch(r ->
                checkIn.isBefore(r.getCheckOutDate()) &&
                checkOut.isAfter(r.getCheckInDate())
        );
    }
}

