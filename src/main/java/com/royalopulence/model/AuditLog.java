package com.royalopulence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String auditLogId;

    private String action;
    private String referenceId;
    private long timestamp;
}

