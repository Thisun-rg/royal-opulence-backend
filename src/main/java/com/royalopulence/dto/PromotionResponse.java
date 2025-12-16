package com.royalopulence.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionResponse {

    private String id; 
    private String title;
    private String description;
    private int discount;
    private String userId;

    // ✅ NEW FIELD
    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;
}
