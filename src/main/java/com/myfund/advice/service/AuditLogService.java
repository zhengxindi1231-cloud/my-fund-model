package com.myfund.advice.service;

import com.myfund.advice.model.AuditLog;
import com.myfund.advice.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 审计日志服务，用于记录用户问题、提示上下文与模型输出。
 */
@Service
public class AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void record(AuditLog auditLog) {
        repository.save(auditLog);
    }
}
