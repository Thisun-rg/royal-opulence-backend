package com.royalopulence.model.operation;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String reservationId;
    private Double amount;
    private String currency;

    private String status;    // PENDING, SUCCESS, FAILED
    private String method;    // NOT_SET, STRIPE, PAYHERE

    private String description;
    private Long createdAt;
}
