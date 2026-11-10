package com.bbu.attendance.audit.controller;

import com.bbu.attendance.audit.dto.AuditLogResponse;
import com.bbu.attendance.audit.service.AuditLogService;
import com.bbu.attendance.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getRecentLogs() {
        List<AuditLogResponse> logs = auditLogService.getRecentAuditLogs();
        return ResponseEntity.ok(ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .message("ទាញយកកំណត់ត្រាសវនកម្ម ៥០ ជួរចុងក្រោយបានជោគជ័យ!")
                .data(logs)
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByUser(@PathVariable Long userId) {
        List<AuditLogResponse> logs = auditLogService.getLogsByUser(userId);
        return ResponseEntity.ok(ApiResponse.<List<AuditLogResponse>>builder()
                .success(true)
                .message("ទាញយកកំណត់ត្រាសវនកម្មរបស់អ្នកប្រើប្រាស់បានជោគជ័យ!")
                .data(logs)
                .build());
    }
}
