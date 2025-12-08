package com.royalopulence.model.core;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.*;

@Document(collection = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    private String id;

    private String roomNumber;
    private String type;
    private String description;
    private double pricePerNight;
    private int capacity;
    private String status; // AVAILABLE / BOOKED / MAINTENANCE
    private List<String> amenities;
}

