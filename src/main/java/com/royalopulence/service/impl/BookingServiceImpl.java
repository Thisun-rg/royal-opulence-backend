package com.royalopulence.service.impl;

<<<<<<< HEAD
import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
import com.royalopulence.model.core.*;
import com.royalopulence.repository.*;
import com.royalopulence.service.base.BookingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
=======
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.royalopulence.dto.booking.BookingRequest;
import com.royalopulence.dto.booking.BookingResponse;
import com.royalopulence.dto.booking.CancelBookingResponse;
import com.royalopulence.dto.payment.PaymentRequest;
import com.royalopulence.dto.payment.PaymentResponse;
import com.royalopulence.model.core.Reservation;
import com.royalopulence.model.core.Reservation.ReservationStatus;
import com.royalopulence.model.core.Room;
import com.royalopulence.model.core.RoomType;
import com.royalopulence.repository.ReservationRepository;
import com.royalopulence.repository.RoomRepository;
import com.royalopulence.repository.RoomTypeRepository;
import com.royalopulence.service.base.BookingService;
import com.royalopulence.service.base.PaymentService;

import lombok.RequiredArgsConstructor;
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ReservationRepository reservationRepository;

<<<<<<< HEAD
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
=======
    private final PaymentService paymentService;

    private static final double TAX_RATE = 0.10;
    private static final double FULL_CAPACITY_MULTIPLIER = 1.25; // +25%
    private static final int MAX_GUESTS_PER_ROOM = 2;

    @Override
    public BookingResponse createReservation(String userId, BookingRequest request) {

        Integer roomsReqObj = request.getRooms();
        Integer guestsReqObj = request.getGuests();

        int roomsRequested = (roomsReqObj == null ? 1 : roomsReqObj);
        int guestsRequested = (guestsReqObj == null ? 1 : guestsReqObj);

        if (roomsRequested <= 0) throw new RuntimeException("Rooms must be at least 1");
        if (guestsRequested <= 0) throw new RuntimeException("Guests must be at least 1");

        if (guestsRequested > roomsRequested * MAX_GUESTS_PER_ROOM) {
            throw new RuntimeException("Max 2 guests per room. Reduce guests or increase rooms.");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) throw new RuntimeException("Invalid booking dates");

        // ✅ Accept CODE (EXECUTIVE) or MongoId (24 hex)
        RoomType roomType = resolveRoomType(request.getRoomTypeId());

        // ✅ IMPORTANT: rooms.roomTypeId stores RoomType._id
        List<Room> availableRooms = roomRepository.findByRoomTypeIdAndStatus(roomType.getId(), "AVAILABLE");

        if (availableRooms.size() < roomsRequested) {
            throw new RuntimeException("Not enough rooms available for selected room type");
        }

        List<Room> selectedRooms = availableRooms.subList(0, roomsRequested);
        List<String> roomIds = selectedRooms.stream().map(Room::getId).toList();

        double baseAmount = nights * roomType.getPricePerNight() * roomsRequested;

        boolean fullCapacity = (guestsRequested == roomsRequested * MAX_GUESTS_PER_ROOM);
        double subTotal = baseAmount * (fullCapacity ? FULL_CAPACITY_MULTIPLIER : 1.0);

        double taxAmount = subTotal * TAX_RATE;
        double totalAmount = subTotal + taxAmount;

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .roomIds(roomIds)
                .roomTypeId(roomType.getId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .rooms(roomsRequested)
                .guests(guestsRequested)
                .totalAmount(totalAmount)
                .status(ReservationStatus.PENDING_PAYMENT)
                .createdAt(System.currentTimeMillis())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        selectedRooms.forEach(r -> r.setStatus("RESERVED"));
        roomRepository.saveAll(selectedRooms);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId(savedReservation.getId());
        paymentRequest.setAmount(totalAmount);
        paymentRequest.setCurrency("LKR");
        paymentRequest.setDescription("Room booking payment");

        PaymentResponse paymentResponse = paymentService.createStripePayment(paymentRequest);


        // Optional: store payment fields inside reservation (only if your Reservation has these fields)
        savedReservation.setPaymentId(paymentResponse.getId());
        savedReservation.setPaymentStatus(paymentResponse.getStatus());
        reservationRepository.save(savedReservation);

        return BookingResponse.builder()
        .reservationId(savedReservation.getId())
        .roomIds(roomIds)
        .roomTypeId(roomType.getId())
        .rooms(roomsRequested)
        .guests(guestsRequested)
        .checkInDate(savedReservation.getCheckInDate())
        .checkOutDate(savedReservation.getCheckOutDate())
        .totalAmount(totalAmount)
        .status(savedReservation.getStatus().name())
        .paymentId(paymentResponse.getId())
        .paymentStatus(paymentResponse.getStatus())

        // ✅ IMPORTANT: send to frontend Checkout page
        .clientSecret(paymentResponse.getStripeClientSecret())

        .build();

    }

    @Override
    public CancelBookingResponse cancelReservation(String reservationId, String userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this booking");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return CancelBookingResponse.builder()
                    .reservationId(reservation.getId())
                    .previousStatus(reservation.getStatus().name())
                    .newStatus(reservation.getStatus().name())
                    .totalAmount(reservation.getTotalAmount())
                    .refundAmount(0)
                    .refundPercentage(0)
                    .message("Booking already cancelled")
                    .build();
        }

        LocalDateTime checkInDateTime = reservation.getCheckInDate().atTime(LocalTime.NOON);
        long hoursBefore = ChronoUnit.HOURS.between(LocalDateTime.now(), checkInDateTime);

        double refundPercentage;
        if (hoursBefore >= 48) refundPercentage = 1.0;
        else if (hoursBefore >= 24) refundPercentage = 0.5;
        else refundPercentage = 0.0;

        double refundAmount = reservation.getTotalAmount() * refundPercentage;

        ReservationStatus previousStatus = reservation.getStatus();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        if (reservation.getRoomIds() != null && !reservation.getRoomIds().isEmpty()) {
            List<Room> roomsToFree = roomRepository.findAllById(reservation.getRoomIds());
            roomsToFree.forEach(r -> r.setStatus("AVAILABLE"));
            roomRepository.saveAll(roomsToFree);
        }

        PaymentResponse paymentResponse = paymentService.refundByReservation(reservationId, refundAmount);

        return CancelBookingResponse.builder()
                .reservationId(reservation.getId())
                .previousStatus(previousStatus.name())
                .newStatus(ReservationStatus.CANCELLED.name())
                .paymentStatus(paymentResponse.getStatus())
                .totalAmount(reservation.getTotalAmount())
                .refundAmount(refundAmount)
                .refundPercentage(refundPercentage)
                .message(refundPercentage == 0.0
                        ? "Cancelled. No refund due to policy (<24h)."
                        : "Cancelled. Refund processed: " + (int) (refundPercentage * 100) + "%")
                .build();
    }

    private RoomType resolveRoomType(String input) {
        if (input == null || input.isBlank()) {
            throw new RuntimeException("roomTypeId is required");
        }

        boolean looksLikeObjectId = input.matches("^[a-fA-F0-9]{24}$");

        if (looksLikeObjectId) {
            return roomTypeRepository.findById(input)
                    .orElseThrow(() -> new RuntimeException("Room type not found: " + input));
        }

        return roomTypeRepository.findByCodeIgnoreCase(input)
                .orElseThrow(() -> new RuntimeException("Room type not found: " + input));
>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
    }
}

