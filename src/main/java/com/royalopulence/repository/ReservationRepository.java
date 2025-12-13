
package com.royalopulence.repository;

import com.royalopulence.model.core.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM Reservation r
        WHERE r.room.id = :roomId
        AND r.status IN ('PENDING','CONFIRMED','CHECKED_IN')
        AND (:start < r.checkOut AND :end > r.checkIn)
    """)
    boolean existsConflict(
            @Param("roomId") Long roomId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.createdAt <= :threshold")
    List<Reservation> findExpiredPending(@Param("threshold") LocalDateTime threshold);

    List<Reservation> findByRoomId(Long roomId);
}
