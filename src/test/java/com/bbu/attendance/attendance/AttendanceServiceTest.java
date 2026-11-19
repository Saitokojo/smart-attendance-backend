package com.bbu.attendance.attendance;

import com.bbu.attendance.attendance.dto.AttendanceCheckInRequest;
import com.bbu.attendance.attendance.dto.AttendanceCheckInResponse;
import com.bbu.attendance.attendance.entity.AttendanceRecord;
import com.bbu.attendance.attendance.repository.AttendanceRepository;
import com.bbu.attendance.attendance.service.AttendanceService;
import com.bbu.attendance.attendance.service.impl.AttendanceServiceImpl;
import com.bbu.attendance.common.exception.InvalidQrException;
import com.bbu.attendance.common.exception.QrExpiredException;
import com.bbu.attendance.qr.dto.DynamicQrPayload;
import com.bbu.attendance.qr.service.QrCryptoService;
import com.bbu.attendance.qr.service.QrNonceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for AttendanceService (The 10-Step Verification Pipeline Engine)")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private QrCryptoService qrCryptoService;

    @Mock
    private QrNonceService qrNonceService;

    private AttendanceService attendanceService;

    // Room LAB-302 reference coordinates
    private static final BigDecimal ROOM_LAT = new BigDecimal("11.55637380");
    private static final BigDecimal ROOM_LON = new BigDecimal("104.92820990");

    @BeforeEach
    void setUp() {
        attendanceService = new AttendanceServiceImpl(attendanceRepository, qrCryptoService, qrNonceService);
        ReflectionTestUtils.setField(attendanceService, "validitySeconds", 15);
        ReflectionTestUtils.setField(attendanceService, "clockSkewSeconds", 2);
    }

    @Test
    @DisplayName("1. checkInStudent when all 10 steps pass should record attendance and return PRESENT")
    void testCheckInStudent_all10StepsPass_shouldSucceed() {
        Long sessionId = 101L;
        String studentUsername = "sokha_student";
        String clientIp = "192.168.1.50";
        String nonce = UUID.randomUUID().toString();
        long currentTimestamp = Instant.now().getEpochSecond();

        AttendanceCheckInRequest request = AttendanceCheckInRequest.builder()
                .sessionId(sessionId)
                .qrToken("valid-base64-qr-token")
                .deviceUuid("device-uuid-12345")
                .latitude(ROOM_LAT)
                .longitude(ROOM_LON)
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode("CS-301")
                .timestamp(currentTimestamp)
                .nonce(nonce)
                .signature("valid-hmac-signature")
                .build();

        when(qrCryptoService.decodePayloadFromString("valid-base64-qr-token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(true);
        when(attendanceRepository.existsBySessionIdAndStudentId(sessionId, 1L)).thenReturn(false);

        AttendanceRecord savedRecord = AttendanceRecord.builder()
                .id(55L)
                .sessionId(sessionId)
                .studentId(1L)
                .studentUsername(studentUsername)
                .status("PRESENT")
                .verifiedAt(LocalDateTime.now())
                .build();

        when(attendanceRepository.save(any(AttendanceRecord.class))).thenReturn(savedRecord);

        AttendanceCheckInResponse response = attendanceService.checkInStudent(request, studentUsername, clientIp);

        assertNotNull(response);
        assertEquals("PRESENT", response.getStatus());
        assertEquals(studentUsername, response.getStudentUsername());
        assertTrue(response.getVerificationSummary().contains("10-STEP VERIFICATION PASSED"));

        verify(qrNonceService, times(1)).validateAndConsumeNonce(nonce);
        verify(attendanceRepository, times(1)).save(any(AttendanceRecord.class));
    }

    @Test
    @DisplayName("2. checkInStudent with Session ID mismatch should throw InvalidQrException")
    void testCheckInStudent_sessionIdMismatch_shouldThrowInvalidQrException() {
        Long requestSessionId = 101L;
        Long qrSessionId = 999L; // Mismatch

        AttendanceCheckInRequest request = AttendanceCheckInRequest.builder()
                .sessionId(requestSessionId)
                .qrToken("token")
                .deviceUuid("uuid")
                .latitude(ROOM_LAT)
                .longitude(ROOM_LON)
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(qrSessionId)
                .timestamp(Instant.now().getEpochSecond())
                .nonce("nonce")
                .build();

        when(qrCryptoService.decodePayloadFromString("token")).thenReturn(payload);

        InvalidQrException ex = assertThrows(InvalidQrException.class, () ->
                attendanceService.checkInStudent(request, "student", "127.0.0.1"));

        assertTrue(ex.getMessage().contains("មិនត្រូវគ្នានឹង Session"));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("3. checkInStudent with tampered HMAC signature should throw InvalidQrException")
    void testCheckInStudent_tamperedSignature_shouldThrowInvalidQrException() {
        Long sessionId = 101L;

        AttendanceCheckInRequest request = AttendanceCheckInRequest.builder()
                .sessionId(sessionId)
                .qrToken("tampered-token")
                .deviceUuid("uuid")
                .latitude(ROOM_LAT)
                .longitude(ROOM_LON)
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .timestamp(Instant.now().getEpochSecond())
                .nonce("nonce")
                .signature("invalid-sig")
                .build();

        when(qrCryptoService.decodePayloadFromString("tampered-token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(false);

        InvalidQrException ex = assertThrows(InvalidQrException.class, () ->
                attendanceService.checkInStudent(request, "student", "127.0.0.1"));

        assertTrue(ex.getMessage().contains("ការផ្ទៀងផ្ទាត់ហត្ថលេខាឌីជីថលបានបរាជ័យ"));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("4. checkInStudent with expired QR (>15s + clock skew) should throw QrExpiredException")
    void testCheckInStudent_expiredQr_shouldThrowQrExpiredException() {
        Long sessionId = 101L;
        long oldTimestamp = Instant.now().getEpochSecond() - 30; // 30s ago (> 17s allowed)

        AttendanceCheckInRequest request = AttendanceCheckInRequest.builder()
                .sessionId(sessionId)
                .qrToken("expired-token")
                .deviceUuid("uuid")
                .latitude(ROOM_LAT)
                .longitude(ROOM_LON)
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .timestamp(oldTimestamp)
                .nonce("nonce")
                .build();

        when(qrCryptoService.decodePayloadFromString("expired-token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(true);

        QrExpiredException ex = assertThrows(QrExpiredException.class, () ->
                attendanceService.checkInStudent(request, "student", "127.0.0.1"));

        assertTrue(ex.getMessage().contains("ផុតកំណត់សុពលភាព"));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("5. checkInStudent when student is outside 40.0m geofence should throw RuntimeException")
    void testCheckInStudent_outsideGeofence_shouldThrowRuntimeException() {
        Long sessionId = 101L;
        // Student is ~177m away
        BigDecimal farLat = new BigDecimal("11.558000");
        BigDecimal farLon = new BigDecimal("104.928200");

        AttendanceCheckInRequest request = AttendanceCheckInRequest.builder()
                .sessionId(sessionId)
                .qrToken("valid-token")
                .deviceUuid("uuid")
                .latitude(farLat)
                .longitude(farLon)
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .timestamp(Instant.now().getEpochSecond())
                .nonce("nonce")
                .build();

        when(qrCryptoService.decodePayloadFromString("valid-token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                attendanceService.checkInStudent(request, "student", "127.0.0.1"));

        assertTrue(ex.getMessage().contains("អ្នកស្ថិតនៅក្រៅបរិវេណបន្ទប់រៀន"));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("6. checkInStudent when student attempts duplicate check-in should throw RuntimeException")
    void testCheckInStudent_duplicateCheckIn_shouldThrowRuntimeException() {
        Long sessionId = 101L;

        AttendanceCheckInRequest request = AttendanceCheckInRequest.builder()
                .sessionId(sessionId)
                .qrToken("valid-token")
                .deviceUuid("uuid")
                .latitude(ROOM_LAT)
                .longitude(ROOM_LON)
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .timestamp(Instant.now().getEpochSecond())
                .nonce("nonce")
                .build();

        when(qrCryptoService.decodePayloadFromString("valid-token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(true);
        when(attendanceRepository.existsBySessionIdAndStudentId(sessionId, 1L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                attendanceService.checkInStudent(request, "student", "127.0.0.1"));

        assertTrue(ex.getMessage().contains("រួចរាល់ហើយ"));
        verify(attendanceRepository, never()).save(any());
    }

    @Test
    @DisplayName("7. getSessionRoster should delegate cleanly to attendanceRepository")
    void testGetSessionRoster_shouldReturnList() {
        Long sessionId = 101L;
        List<AttendanceRecord> expectedList = List.of(
                AttendanceRecord.builder().id(1L).sessionId(sessionId).studentUsername("sokha").build()
        );

        when(attendanceRepository.findBySessionIdOrderByVerifiedAtDesc(sessionId)).thenReturn(expectedList);

        List<AttendanceRecord> actualList = attendanceService.getSessionRoster(sessionId);

        assertEquals(1, actualList.size());
        assertEquals("sokha", actualList.get(0).getStudentUsername());
        verify(attendanceRepository, times(1)).findBySessionIdOrderByVerifiedAtDesc(sessionId);
    }
}
