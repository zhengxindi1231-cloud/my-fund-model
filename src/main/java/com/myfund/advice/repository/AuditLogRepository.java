package com.myfund.advice.repository;

import com.myfund.advice.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 审计日志持久化接口。
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
