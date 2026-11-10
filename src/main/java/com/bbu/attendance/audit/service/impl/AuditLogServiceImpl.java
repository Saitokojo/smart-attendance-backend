package com.bbu.attendance.audit.service.impl;

import com.bbu.attendance.audit.dto.AuditLogResponse;
import com.bbu.attendance.audit.entity.AuditLog;
import com.bbu.attendance.audit.repository.AuditLogRepository;
import com.bbu.attendance.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Async
    @Override
    @Transactional
    public void logAsync(Long userId, String username, String action, String details, String ipAddress, Long executionTimeMs, String status) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .details(details)
                    .ipAddress(ipAddress)
                    .executionTimeMs(executionTimeMs)
                    .status(status)
                    .build();

            auditLogRepository.save(auditLog);
            log.info("⚡ [AuditLog-VirtualThread] Action: {} | User: {} | Latency: {}ms", action, username, executionTimeMs);
        } catch (Exception e) {
            log.error("❌ Failed to save async audit log: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getRecentAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsByUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getUserId(),
                log.getUsername(),
                log.getAction(),
                log.getDetails(),
                log.getIpAddress(),
                log.getExecutionTimeMs(),
                log.getStatus(),
                log.getTimestamp()
        );
    }
}
