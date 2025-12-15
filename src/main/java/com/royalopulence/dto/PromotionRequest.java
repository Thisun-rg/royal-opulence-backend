package com.royalopulence.dto;

import lombok.Data;
import java.util.Date;

@Data
public class PromotionRequest {
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private boolean active;

    // ⭐ NEW
    private String targetUserId; // optional
}
