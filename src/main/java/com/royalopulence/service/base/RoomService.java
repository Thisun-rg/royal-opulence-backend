
package com.royalopulence.service.base;

import java.util.List;

import com.royalopulence.dto.room.RoomRequest;
import com.royalopulence.dto.room.RoomResponse;


public interface RoomService {
    RoomResponse createRoom(RoomRequest request);
    List<RoomResponse> getAvailableRooms();
    void updateRoomStatus(String roomId, String status);
}

