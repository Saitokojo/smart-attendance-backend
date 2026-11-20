package com.bbu.attendance.qr.controller;

import com.bbu.attendance.attendance.entity.AttendanceRecord;
import com.bbu.attendance.attendance.service.AttendanceService;
import com.bbu.attendance.dto.ApiResponse;
import com.bbu.attendance.qr.dto.GenerateQrResponse;
import com.bbu.attendance.qr.dto.ProjectorQrResponse;
import com.bbu.attendance.qr.service.DynamicQrService;
import com.bbu.attendance.qr.service.QrCodeImageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller សម្រាប់ Teacher Fullscreen Web Projector
 * - GET /api/qr/projector/{sessionId} (ផ្តល់ QR Image Base64 & Countdown 15s)
 * - GET /api/qr/projector/{sessionId}/live-roster (បញ្ជីសិស្សមានវត្តមាន Realtime)
 * ទទួលខុសត្រូវដោយ៖ និស្សិត សេង ស៊ីមផា (ថ្ងៃសុក្រ)
 */
@RestController
@RequestMapping("/api/qr/projector")
@RequiredArgsConstructor
public class ProjectorViewController {

    private final DynamicQrService dynamicQrService;
    private final QrCodeImageGenerator qrCodeImageGenerator;
    private final AttendanceService attendanceService;

    @GetMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ProjectorQrResponse>> getProjectorQr(@PathVariable Long sessionId) {
        GenerateQrResponse qr = dynamicQrService.generateRollingQr(sessionId);
        String base64Image = qrCodeImageGenerator.generateBase64QrImage(qr.getQrToken(), 450, 450);

        ProjectorQrResponse response = ProjectorQrResponse.builder()
                .sessionId(sessionId)
                .className("CS-301: Advanced Software Engineering")
                .roomCode("LAB-302")
                .qrBase64Image(base64Image)
                .qrToken(qr.getQrToken())
                .countdownSeconds(qr.getExpiresInSeconds())
                .refreshIntervalMs(15000L)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Projector QR payload retrieved", response));
    }

    @GetMapping("/{sessionId}/live-roster")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getLiveRoster(@PathVariable Long sessionId) {
        List<AttendanceRecord> roster = attendanceService.getSessionRoster(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Live Roster Stream", roster));
    }
}
