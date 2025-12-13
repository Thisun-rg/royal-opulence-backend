package com.royalopulence.repository;

import com.royalopulence.model.operation.Reservation;
import com.royalopulence.model.operation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Check if any reservation exists for the given room that overlaps the given date range
     * and whose status is in the provided statuses list.
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reservation r " +
           "WHERE r.room.id = :roomId " +
           "AND r.status IN :statuses " +
           "AND (:start < r.checkOut AND :end > r.checkIn)")
    boolean existsConflict(
            @Param("roomId") Long roomId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("statuses") List<ReservationStatus> statuses
    );

    /**
     * Find pending reservations created before (or equal) to threshold.
     * Pass ReservationStatus.PENDING as the status parameter.
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.createdAt <= :threshold")
    List<Reservation> findExpiredPending(
            @Param("status") ReservationStatus status,
            @Param("threshold") LocalDateTime threshold
    );

    List<Reservation> findByRoomId(Long roomId);
}
