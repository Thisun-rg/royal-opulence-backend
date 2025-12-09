package com.royalopulence.dto.room;

import java.math.BigDecimal;

public class RoomResponse {
private Long id;
private String roomNumber;
private String roomType;
private int capacity;
private BigDecimal pricePerNight;
private String amenities;


// constructors
public RoomResponse() {}


public RoomResponse(Long id, String roomNumber, String roomType, int capacity, BigDecimal pricePerNight, String amenities) {
this.id = id;
this.roomNumber = roomNumber;
this.roomType = roomType;
this.capacity = capacity;
this.pricePerNight = pricePerNight;
this.amenities = amenities;
}


// getters/setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }


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