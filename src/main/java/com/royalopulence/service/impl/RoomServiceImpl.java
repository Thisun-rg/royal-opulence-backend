package com.royalopulence.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.royalopulence.model.core.Room;
import com.royalopulence.repository.RoomRepository;
import com.royalopulence.dto.room.RoomRequest;
import com.royalopulence.dto.room.RoomResponse;
import com.royalopulence.service.base.RoomService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public RoomResponse createRoom(RoomRequest request) {

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomTypeId(request.getRoomTypeId())
                .status("AVAILABLE")
                .build();

        Room saved = roomRepository.save(room);

        return RoomResponse.builder()
                .id(saved.getId())
                .roomNumber(saved.getRoomNumber())
                .roomTypeId(saved.getRoomTypeId())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public List<RoomResponse> getAvailableRooms() {
<<<<<<< HEAD
        return roomRepository.findByStatus("AVAILABLE")
=======
        return roomRepository.findByStatusIgnoreCase("AVAILABLE")

>>>>>>> ae1e56b3ddb624b1593dca097864cefa37c50ab3
                .stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .roomNumber(room.getRoomNumber())
                        .roomTypeId(room.getRoomTypeId())
                        .status(room.getStatus())
                        .build())
                .toList();
    }

    @Override
    public void updateRoomStatus(String roomId, String status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(status);
        roomRepository.save(room);
    }
}
