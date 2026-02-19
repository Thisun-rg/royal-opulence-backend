package com.royalopulence.model.core;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rooms")
public class Room {

    @Id
    private String id;

    private String roomNumber;
    private String roomTypeId;  // Link to RoomType
    private String status;      // AVAILABLE, RESERVED, MAINTENANCE
}
