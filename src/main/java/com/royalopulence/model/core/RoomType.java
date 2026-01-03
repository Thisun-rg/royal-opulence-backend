package com.royalopulence.model.core;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "room_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

    @Id
    private String id;

    private String name;

    private double pricePerNight;

    // Payment-related (from Sanduni)
    private boolean refundable;
}
