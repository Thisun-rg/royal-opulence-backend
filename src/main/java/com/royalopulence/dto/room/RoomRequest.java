package com.royalopulence.dto.room;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public class RoomRequest {


@NotBlank
private String roomNumber;


@NotBlank
private String roomType;


@Min(1)
private int capacity;


@NotNull
private BigDecimal pricePerNight;


private String amenities;


// getters + setters
public String getRoomNumber() { return roomNumber; }
public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }


public String getRoomType() { return roomType; }
public void setRoomType(String roomType) { this.roomType = roomType; }


public int getCapacity() { return capacity; }
public void setCapacity(int capacity) { this.capacity = capacity; }


public BigDecimal getPricePerNight() { return pricePerNight; }
public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }


public String getAmenities() { return amenities; }
public void setAmenities(String amenities) { this.amenities = amenities; }
}
