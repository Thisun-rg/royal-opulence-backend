package com.royalopulence.service.impl;
import com.royalopulence.dto.room.RoomResponse;
import com.royalopulence.model.core.Room;
import com.royalopulence.repository.RoomRepository;
import com.royalopulence.service.base.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // ---------------- CREATE ROOM ----------------

    @Override
    public RoomResponse createRoom(Room room) {
        Room saved = roomRepository.save(room);
        return mapToResponse(saved);
    }

    // ---------------- GET ROOM BY ID ----------------

    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        return mapToResponse(room);
    }

    // ---------------- LIST ALL ROOMS ----------------

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- SEARCH AVAILABLE ROOMS ----------------

    @Override
    public List<RoomResponse> searchAvailableRooms(
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests
    ) {

        List<Room> rooms = roomRepository.findAvailableRooms(checkIn, checkOut);

        if (guests != null && guests > 0) {
            rooms = rooms.stream()
                    .filter(r -> r.getCapacity() >= guests)
                    .collect(Collectors.toList());
        }

        return rooms.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---------------- HELPER ----------------

    private RoomResponse mapToResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomType(room.getRoomType());
        response.setCapacity(room.getCapacity());
        response.setPricePerNight(room.getPricePerNight());
        response.setAmenities(room.getAmenities());
        return response;
    }
}
