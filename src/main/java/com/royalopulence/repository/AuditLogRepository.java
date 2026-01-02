package com.royalopulence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalopulence.model.AuditLog;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> 
    {
        
    }

