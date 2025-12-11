package com.royalopulence.service.impl;

package com.royalopulence.service.impl;


import com.royalopulence.dto.room.RoomResponse;
import com.royalopulence.model.operation.Room;
import com.royalopulence.repository.RoomRepository;
import com.royalopulence.service.base.RoomService;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RoomServiceImpl implements RoomService {


private final RoomRepository roomRepository;


public RoomServiceImpl(RoomRepository roomRepository) {
this.roomRepository = roomRepository;
}


@Override
public List<RoomResponse> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer guests) {
List<Room> rooms = roomRepository.findAvailableRooms(checkIn, checkOut);
if (guests != null && guests > 0) {
rooms = rooms.stream().filter(r -> r.getCapacity() >= guests).collect(Collectors.toList());
}
return rooms.stream().map(r -> new RoomResponse(
r.getId(), r.getRoomNumber(), r.getRoomType(), r.getCapacity(), r.getPricePerNight(), r.getAmenities()
)).collect(Collectors.toList());
}
}