package com.royalopulence.model.core;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "promotions")
public class Promotion {

    @Id
    private String id;

    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private boolean active;

    // ⭐ NEW
    private String targetUserId; // null = general promotion

    public Promotion() {}

    public Promotion(String title, String description, Date startDate,
                     Date endDate, boolean active, String targetUserId) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.targetUserId = targetUserId;
    }

    // getters & setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }
}
