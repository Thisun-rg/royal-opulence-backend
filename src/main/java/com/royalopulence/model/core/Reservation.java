package com.royalopulence.model.core;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.*;

@Document(collection = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    private String id;

    private String userId;     // Reference to User document
    private String roomId;     // Reference to Room document

    private Date checkInDate;
    private Date checkOutDate;

    private String status;     // PENDING / CONFIRMED / CANCELLED
    private double totalAmount;
}

