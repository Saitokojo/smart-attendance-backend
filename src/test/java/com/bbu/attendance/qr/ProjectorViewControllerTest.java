package com.bbu.attendance.qr;

import com.bbu.attendance.attendance.entity.AttendanceRecord;
import com.bbu.attendance.attendance.service.AttendanceService;
import com.bbu.attendance.dto.ApiResponse;
import com.bbu.attendance.qr.controller.ProjectorViewController;
import com.bbu.attendance.qr.dto.GenerateQrResponse;
import com.bbu.attendance.qr.dto.ProjectorQrResponse;
import com.bbu.attendance.qr.service.DynamicQrService;
import com.bbu.attendance.qr.service.QrCodeImageGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for ProjectorViewController (Teacher Web Projector Screen)")
class ProjectorViewControllerTest {

    @Mock
    private DynamicQrService dynamicQrService;

    @Mock
    private QrCodeImageGenerator qrCodeImageGenerator;

    @Mock
    private AttendanceService attendanceService;

    private ProjectorViewController projectorViewController;

    @BeforeEach
    void setUp() {
        projectorViewController = new ProjectorViewController(dynamicQrService, qrCodeImageGenerator, attendanceService);
    }

    @Test
    @DisplayName("1. getProjectorQr should return ProjectorQrResponse with base64 image and 15s countdown")
    void testGetProjectorQr_shouldReturnResponse() {
        Long sessionId = 101L;

        GenerateQrResponse qr = GenerateQrResponse.builder()
                .sessionId(sessionId)
                .qrToken("test-qr-token")
                .expiresInSeconds(15)
                .build();

        when(dynamicQrService.generateRollingQr(sessionId)).thenReturn(qr);
        when(qrCodeImageGenerator.generateBase64QrImage("test-qr-token", 450, 450))
                .thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...");

        ResponseEntity<ApiResponse<ProjectorQrResponse>> entity = projectorViewController.getProjectorQr(sessionId);

        assertNotNull(entity);
        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertNotNull(entity.getBody());
        assertTrue(entity.getBody().isSuccess());

        ProjectorQrResponse data = entity.getBody().getData();
        assertNotNull(data);
        assertEquals(sessionId, data.getSessionId());
        assertEquals("CS-301: Advanced Software Engineering", data.getClassName());
        assertEquals("LAB-302", data.getRoomCode());
        assertEquals(15, data.getCountdownSeconds());
        assertEquals(15000L, data.getRefreshIntervalMs());
        assertTrue(data.getQrBase64Image().startsWith("data:image/png;base64,"));
    }

    @Test
    @DisplayName("2. getLiveRoster should return list of active session attendance records")
    void testGetLiveRoster_shouldReturnRosterList() {
        Long sessionId = 101L;
        List<AttendanceRecord> mockRoster = List.of(
                AttendanceRecord.builder()
                        .id(1L)
                        .sessionId(sessionId)
                        .studentUsername("sokha_student")
                        .deviceUuid("uuid-12345678")
                        .status("PRESENT")
                        .verifiedAt(LocalDateTime.now())
                        .build()
        );

        when(attendanceService.getSessionRoster(sessionId)).thenReturn(mockRoster);

        ResponseEntity<ApiResponse<List<AttendanceRecord>>> entity = projectorViewController.getLiveRoster(sessionId);

        assertNotNull(entity);
        assertTrue(entity.getStatusCode().is2xxSuccessful());
        assertNotNull(entity.getBody());
        assertEquals(1, entity.getBody().getData().size());
        assertEquals("sokha_student", entity.getBody().getData().get(0).getStudentUsername());
    }
}
