package com.royalopulence.model.core;

import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "rooms")
public class Room {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@Column(nullable = false, unique = true)
private String roomNumber;

@Column(nullable = false)
private String roomType; // Deluxe, Superior, etc.


private int capacity;


private BigDecimal pricePerNight;


private String amenities; // comma separated


// constructors, getters, setters
public Room() {}


public Room(String roomNumber, String roomType, int capacity, BigDecimal pricePerNight, String amenities) {
this.roomNumber = roomNumber;
this.roomType = roomType;
this.capacity = capacity;
this.pricePerNight = pricePerNight;
this.amenities = amenities;

}


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