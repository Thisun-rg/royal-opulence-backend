
package com.royalopulence.repository;

import com.royalopulence.model.core.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByStatus(String status);
    List<Room> findByRoomTypeId(String roomTypeId);
}

