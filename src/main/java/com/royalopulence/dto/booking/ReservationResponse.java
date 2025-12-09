
package com.royalopulence.dto.booking;


import com.royalopulence.model.operation.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;


public class ReservationResponse { 
private Long id;
private Long roomId;
private String roomNumber;
private ReservationStatus status;
private LocalDate checkIn;
private LocalDate checkOut;
private Long userId;
private int guests;
private BigDecimal totalPrice;
private String specialRequests;


// getters/setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }


public Long getRoomId() { return roomId; }
public void setRoomId(Long roomId) { this.roomId = roomId; }


public String getRoomNumber() { return roomNumber; }
public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }


public ReservationStatus getStatus() { return status; }
public void setStatus(ReservationStatus status) { this.status = status; }


public LocalDate getCheckIn() { return checkIn; }
public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }


public LocalDate getCheckOut() { return checkOut; }
public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }


public Long getUserId() { return userId; }
public void setUserId(Long userId) { this.userId = userId; }


public int getGuests() { return guests; }
public void setGuests(int guests) { this.guests = guests; }


public BigDecimal getTotalPrice() { return totalPrice; }
public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }


public String getSpecialRequests() { return specialRequests; }
public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
}