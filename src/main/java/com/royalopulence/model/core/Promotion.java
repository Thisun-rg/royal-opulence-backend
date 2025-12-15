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

    public Promotion() {
        this.active = true;
    }

    public Promotion(String title, String description, Date startDate, Date endDate, boolean active) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

}
