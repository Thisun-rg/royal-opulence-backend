
package com.royalopulence.service.base;

import java.time.LocalDate;
import java.util.List;     //for recognize List

import com.royalopulence.dto.room.RoomResponse;

public interface RoomService {

    List<RoomResponse> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, int guests);

}
