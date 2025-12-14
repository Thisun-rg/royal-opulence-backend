package com.royalopulence.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationResponse {
    private String id;
    private String title;
    private String message;
    private String userId;
    private Date createdAt;
}
