package com.royalopulence.model.core;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.royalopulence.model.operation.ReservationStatus;

@Entity
@Table(name = "reservations")

public class Reservation {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "room_id", nullable = false)
private Room room;


private Long userId; // resolve to actual user via auth module


private LocalDate checkIn;
private LocalDate checkOut;


private int guests;


@Enumerated(EnumType.STRING)
private ReservationStatus status;


private LocalDateTime createdAt;


private BigDecimal totalPrice;
private String specialRequests;


public Reservation() {}


// getters and setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }


public Room getRoom() { return room; }
public void setRoom(Room room) { this.room = room; }


public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }


public LocalDate getCheckIn() { return checkIn; }
public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }


public LocalDate getCheckOut() { return checkOut; }
public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }


public int getGuests() { return guests; }
public void setGuests(int guests) { this.guests = guests; }


public ReservationStatus getStatus() { return status; }
public void setStatus(ReservationStatus status) { this.status = status; }


public LocalDateTime getCreatedAt() { return createdAt; }
public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


public BigDecimal getTotalPrice() { return totalPrice; }
public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }


public String getSpecialRequests() { return specialRequests; }
public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

}
