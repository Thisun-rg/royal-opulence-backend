
package com.royalopulence.dto.booking;


import jakarta.validation.constraints.*;
import java.time.LocalDate;


public class ReservationRequest {


@NotNull
private Long roomId;


@NotNull
private LocalDate checkIn;


@NotNull
private LocalDate checkOut;


@Min(1)
private int guests;


private Long userId; // optional if security extracts


private String specialRequests;


// getters/setters
public Long getRoomId() { return roomId; }
public void setRoomId(Long roomId) { this.roomId = roomId; }


public LocalDate getCheckIn() { return checkIn; }
public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }


public LocalDate getCheckOut() { return checkOut; }
public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }


public int getGuests() { return guests; }
public void setGuests(int guests) { this.guests = guests; }


public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }


public String getSpecialRequests() { return specialRequests; }
public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
}