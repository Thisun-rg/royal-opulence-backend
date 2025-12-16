package com.royalopulence.dto;
import com.royalopulence.model.core.PromotionStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionResponse {

    private String id; 
    private String title;
    private String description;
    private int discount;
    private String userId;

   
    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    private PromotionStatus status;
}
