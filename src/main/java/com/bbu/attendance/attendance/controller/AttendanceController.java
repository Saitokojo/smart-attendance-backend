package com.bbu.attendance.attendance.controller;

import com.bbu.attendance.attendance.dto.AttendanceCheckInRequest;
import com.bbu.attendance.attendance.dto.AttendanceCheckInResponse;
import com.bbu.attendance.attendance.entity.AttendanceRecord;
import com.bbu.attendance.attendance.service.AttendanceService;
import com.bbu.attendance.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AttendanceCheckInResponse>> checkIn(
            @Valid @RequestBody AttendanceCheckInRequest request,
            Authentication authentication,
            HttpServletRequest servletRequest) {

        String studentUsername = authentication.getName();
        String clientIp = servletRequest.getRemoteAddr();

        AttendanceCheckInResponse response = attendanceService.checkInStudent(request, studentUsername, clientIp);
        return ResponseEntity.ok(ApiResponse.success("វត្តមានត្រូវបានកត់ត្រាជោគជ័យ ១០០%", response));
    }

    @GetMapping("/session/{sessionId}/roster")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getSessionRoster(@PathVariable Long sessionId) {
        List<AttendanceRecord> roster = attendanceService.getSessionRoster(sessionId);
        return ResponseEntity.ok(ApiResponse.success("បញ្ជីឈ្មោះសិស្សមានវត្តមាន", roster));
    }
}