
package com.royalopulence.repository;

import com.royalopulence.model.core.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
        SELECT r FROM Room r
        WHERE r.id NOT IN (
            SELECT res.room.id FROM Reservation res
            WHERE (:checkIn < res.checkOut AND :checkOut > res.checkIn)
        )
    """)
    List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut);
}
