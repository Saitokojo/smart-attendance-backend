package com.bbu.attendance.qr.controller;

import com.bbu.attendance.dto.ApiResponse;
import com.bbu.attendance.qr.dto.GenerateQrResponse;
import com.bbu.attendance.qr.dto.VerifyQrRequest;
import com.bbu.attendance.qr.dto.VerifyQrResponse;
import com.bbu.attendance.qr.service.DynamicQrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller សម្រាប់ Dynamic QR Code Endpoints (/api/qr)
 * - GET /api/qr/session/{sessionId}/generate (គ្រូ generate QR Token ថ្មី)
 * - POST /api/qr/verify (សិស្សផ្ទៀងផ្ទាត់ QR Token)
 */
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class DynamicQrController {

    private final DynamicQrService dynamicQrService;

    @GetMapping("/session/{sessionId}/generate")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<GenerateQrResponse>> generateQr(@PathVariable Long sessionId) {
        GenerateQrResponse response = dynamicQrService.generateRollingQr(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Dynamic QR generated successfully", response));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<VerifyQrResponse>> verifyQr(
            @Valid @RequestBody VerifyQrRequest request,
            Authentication authentication) {
        String studentUsername = authentication.getName();
        VerifyQrResponse response = dynamicQrService.verifyStudentScan(request, studentUsername);
        return ResponseEntity.ok(ApiResponse.success("Attendance QR verified", response));
    }
}
