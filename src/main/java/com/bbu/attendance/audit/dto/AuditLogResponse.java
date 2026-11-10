package com.bbu.attendance.audit.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(
    Long id,
    Long userId,
    String username,
    String action,
    String details,
    String ipAddress,
    Long executionTimeMs,
    String status,
    LocalDateTime timestamp
) {}
