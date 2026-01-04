package com.royalopulence.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String title;
    private String message;
    private String userId; // optional
}
