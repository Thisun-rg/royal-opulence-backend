package com.royalopulence.repository;

import com.royalopulence.model.operation.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;


public interface RoomRepository extends JpaRepository<Room, Long> {


// find rooms that are not reserved in the given period
@Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
" SELECT res.room.id FROM Reservation res WHERE res.status IN ('PENDING','CONFIRMED','CHECKED_IN')" +
" AND (:start < res.checkOut AND :end > res.checkIn) )")
List<Room> findAvailableRooms(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
