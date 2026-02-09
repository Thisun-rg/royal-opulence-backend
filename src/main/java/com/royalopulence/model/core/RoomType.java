// RoomType.java
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

    private String code;        // ✅ DELUXE, OCEAN, EXECUTIVE, PRESIDENTIAL
    private String name;        // Deluxe Room, Premier Ocean View...
    private double pricePerNight;
    private boolean refundable;
}
