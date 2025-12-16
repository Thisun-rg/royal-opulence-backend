package com.royalopulence.model.core;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "promotions")
public class Promotion {

    @Id
    private String id;

    private String title;
    private String description;
    private int discount;
    private String userId;

    // ✅ NEW FIELD
    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;
}