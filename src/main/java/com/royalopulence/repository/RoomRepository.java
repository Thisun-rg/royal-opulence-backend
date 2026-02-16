
package com.royalopulence.repository;

import com.royalopulence.model.core.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByStatus(String status);
    List<Room> findByRoomTypeId(String roomTypeId);

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalopulence.model.core.Room;

public interface RoomRepository extends MongoRepository<Room, String> {

    // booking selection
    List<Room> findByRoomTypeIdAndStatus(String roomTypeId, String status);

    // used by RoomServiceImpl (you have an error for this)
    List<Room> findByStatusIgnoreCase(String status);

    // used by RoomSeeder (you have an error for this)
    long countByRoomTypeId(String roomTypeId);

}

