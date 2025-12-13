
package com.royalopulence.model.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "rooms")
public class Room {

    @Id
    private String id;

    private String roomNumber;
    private String roomType;
    private int capacity;
    private BigDecimal pricePerNight;
    private String amenities;

    public Room() {}

    // getters and setters
}
