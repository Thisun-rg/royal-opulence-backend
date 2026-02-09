package com.royalopulence.service.impl;

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

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ReservationRepository reservationRepository;

    // 🔗 Sanduni’s payment module
    private final PaymentService paymentService;

    private static final double TAX_RATE = 0.10;
    private static final double FULL_CAPACITY_MULTIPLIER = 1.25; // +25%
    private static final int MAX_GUESTS_PER_ROOM = 2;

    // =========================================================
    // CREATE RESERVATION
    // =========================================================
    @Override
    public BookingResponse createReservation(String userId, BookingRequest request) {

        Integer roomsReqObj = request.getRooms();
Integer guestsReqObj = request.getGuests();

int roomsRequested = (roomsReqObj == null ? 1 : roomsReqObj);
int guestsRequested = (guestsReqObj == null ? 1 : guestsReqObj);


        if (roomsRequested <= 0) throw new RuntimeException("Rooms must be at least 1");
        if (guestsRequested <= 0) throw new RuntimeException("Guests must be at least 1");

        // ✅ Max 2 guests per room
        if (guestsRequested > roomsRequested * MAX_GUESTS_PER_ROOM) {
            throw new RuntimeException("Max 2 guests per room. Reduce guests or increase rooms.");
        }

        // 1️⃣ Calculate nights
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) throw new RuntimeException("Invalid booking dates");

        // 2️⃣ Resolve room type (accept CODE or MongoId)
        RoomType roomType = resolveRoomType(request.getRoomTypeId());

        // 3️⃣ Pick N available rooms by REAL roomTypeId (Mongo _id)
        List<Room> availableRooms = roomRepository.findByRoomTypeIdAndStatus(roomType.getId(), "AVAILABLE");

        if (availableRooms.size() < roomsRequested) {
            throw new RuntimeException("Not enough rooms available for selected room type");
        }

        List<Room> selectedRooms = availableRooms.subList(0, roomsRequested);
        List<String> roomIds = selectedRooms.stream().map(Room::getId).toList();

        // 4️⃣ Price calculation (backend matches frontend)
        double baseAmount = nights * roomType.getPricePerNight() * roomsRequested;

        boolean fullCapacity = (guestsRequested == roomsRequested * MAX_GUESTS_PER_ROOM);
        double subTotal = baseAmount * (fullCapacity ? FULL_CAPACITY_MULTIPLIER : 1.0);

        double taxAmount = subTotal * TAX_RATE;
        double totalAmount = subTotal + taxAmount;

        // 5️⃣ Save reservation with roomIds
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

        // 6️⃣ Mark all selected rooms as RESERVED
        selectedRooms.forEach(r -> r.setStatus("RESERVED"));
        roomRepository.saveAll(selectedRooms);

        // 7️⃣ Create payment (Sanduni module)
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setReservationId(savedReservation.getId());
        paymentRequest.setAmount(totalAmount);
        paymentRequest.setCurrency("LKR");
        paymentRequest.setDescription("Room booking payment");

        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        // (Optional but recommended) store payment info in reservation
        savedReservation.setPaymentId(paymentResponse.getId());
        savedReservation.setPaymentStatus(paymentResponse.getStatus());
        reservationRepository.save(savedReservation);

        // 8️⃣ Response
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
                .build();
    }

    // =========================================================
    // CANCEL RESERVATION
    // =========================================================
    @Override
    public CancelBookingResponse cancelReservation(String reservationId, String userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // 🔐 Ownership check
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this booking");
        }

        // 🔁 Already cancelled
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

        // ⏱ Cancellation policy (check-in at noon)
        LocalDateTime checkInDateTime = reservation.getCheckInDate().atTime(LocalTime.NOON);
        long hoursBefore = ChronoUnit.HOURS.between(LocalDateTime.now(), checkInDateTime);

        double refundPercentage;
        if (hoursBefore >= 48) refundPercentage = 1.0;
        else if (hoursBefore >= 24) refundPercentage = 0.5;
        else refundPercentage = 0.0;

        double refundAmount = reservation.getTotalAmount() * refundPercentage;

        // 1️⃣ Update reservation
        ReservationStatus previousStatus = reservation.getStatus();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        // 2️⃣ Free ALL rooms
        if (reservation.getRoomIds() != null && !reservation.getRoomIds().isEmpty()) {
            List<Room> roomsToFree = roomRepository.findAllById(reservation.getRoomIds());
            roomsToFree.forEach(r -> r.setStatus("AVAILABLE"));
            roomRepository.saveAll(roomsToFree);
        }

        // 3️⃣ Refund (if applicable)
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

    // =========================================================
    // HELPERS
    // =========================================================
    private RoomType resolveRoomType(String input) {
        if (input == null || input.isBlank()) {
            throw new RuntimeException("roomTypeId is required");
        }

        // If looks like Mongo ObjectId
        boolean looksLikeObjectId = input.matches("^[a-fA-F0-9]{24}$");

        if (looksLikeObjectId) {
            return roomTypeRepository.findById(input)
                    .orElseThrow(() -> new RuntimeException("Room type not found: " + input));
        }

        // Else treat as CODE (DELUXE/EXECUTIVE/etc.) or NAME (if you use name)
        // ✅ Ensure you have findByCodeIgnoreCase in repository
        return roomTypeRepository.findByCodeIgnoreCase(input)
                .orElseThrow(() -> new RuntimeException("Room type not found: " + input));
    }
}
