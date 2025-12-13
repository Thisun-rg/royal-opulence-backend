package com.royalopulence.util;

import org.springframework.stereotype.Component;

import com.royalopulence.model.AuditLog;
import com.royalopulence.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditUtil {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String referenceId) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setReferenceId(referenceId);
        log.setTimestamp(System.currentTimeMillis());
        auditLogRepository.save(log);
    }
}
