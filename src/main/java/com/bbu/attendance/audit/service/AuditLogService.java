package com.bbu.attendance.audit.service;

import com.bbu.attendance.audit.dto.AuditLogResponse;
import java.util.List;

public interface AuditLogService {
    void logAsync(Long userId, String username, String action, String details, String ipAddress, Long executionTimeMs, String status);
    List<AuditLogResponse> getRecentAuditLogs();
    List<AuditLogResponse> getLogsByUser(Long userId);
}
