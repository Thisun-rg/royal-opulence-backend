
package com.royalopulence.controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

import com.royalopulence.dto.room.RoomRequest;
import com.royalopulence.dto.room.RoomResponse;
import com.royalopulence.service.base.RoomService;


@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public RoomResponse createRoom(@RequestBody RoomRequest request) {
        return roomService.createRoom(request);
    }

    @GetMapping("/available")
    public List<RoomResponse> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    @PutMapping("/{roomId}/status/{status}")
    public String updateStatus(@PathVariable String roomId,
                               @PathVariable String status) {

        roomService.updateRoomStatus(roomId, status);
        return "Room status updated to " + status;
    }
}

