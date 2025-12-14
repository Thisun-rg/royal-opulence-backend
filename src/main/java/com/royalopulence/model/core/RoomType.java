package com.royalopulence.model.core;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "room_types")
public class RoomType {

    @Id
    private String id;

    private String name;           // Deluxe, Suite, Standard
    private String description;
    private double pricePerNight;
}
