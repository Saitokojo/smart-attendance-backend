package com.bbu.attendance.qr;

import com.bbu.attendance.common.exception.InvalidQrException;
import com.bbu.attendance.common.exception.QrExpiredException;
import com.bbu.attendance.qr.dto.DynamicQrPayload;
import com.bbu.attendance.qr.dto.GenerateQrResponse;
import com.bbu.attendance.qr.dto.VerifyQrRequest;
import com.bbu.attendance.qr.dto.VerifyQrResponse;
import com.bbu.attendance.qr.service.DynamicQrService;
import com.bbu.attendance.qr.service.QrCryptoService;
import com.bbu.attendance.qr.service.QrNonceService;
import com.bbu.attendance.qr.service.impl.DynamicQrServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for DynamicQrService (Rolling QR Generation & Verify)")
class DynamicQrServiceTest {

    @Mock
    private QrCryptoService qrCryptoService;

    @Mock
    private QrNonceService qrNonceService;

    private DynamicQrService dynamicQrService;

    @BeforeEach
    void setUp() {
        dynamicQrService = new DynamicQrServiceImpl(qrCryptoService, qrNonceService);
        ReflectionTestUtils.setField(dynamicQrService, "validitySeconds", 15);
        ReflectionTestUtils.setField(dynamicQrService, "clockSkewSeconds", 2);
    }

    @Test
    @DisplayName("1. generateRollingQr should produce signed token and register nonce with 15s TTL")
    void testGenerateRollingQr_shouldProduceToken() {
        Long sessionId = 101L;

        when(qrCryptoService.generateSignature(eq(sessionId), eq("CS-101"), anyLong(), anyString()))
                .thenReturn("mocked-hmac-sig");
        when(qrCryptoService.encodePayloadToString(any(DynamicQrPayload.class)))
                .thenReturn("encoded-url-safe-token");

        GenerateQrResponse response = dynamicQrService.generateRollingQr(sessionId);

        assertNotNull(response);
        assertEquals("encoded-url-safe-token", response.getQrToken());
        assertEquals(sessionId, response.getSessionId());
        assertEquals(15, response.getExpiresInSeconds());
        assertNotNull(response.getNonce());

        verify(qrNonceService, times(1)).registerNonce(anyString(), eq(sessionId), eq(15L));
    }

    @Test
    @DisplayName("2. verifyStudentScan with valid token should succeed and burn nonce")
    void testVerifyStudentScan_validToken_shouldSucceed() {
        Long sessionId = 101L;
        String studentUsername = "sokha_student";
        String nonce = UUID.randomUUID().toString();
        long currentSec = Instant.now().getEpochSecond();

        VerifyQrRequest request = VerifyQrRequest.builder()
                .sessionId(sessionId)
                .qrToken("valid-token")
                .deviceUuid("uuid-123")
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode("CS-101")
                .timestamp(currentSec)
                .nonce(nonce)
                .signature("sig")
                .build();

        when(qrCryptoService.decodePayloadFromString("valid-token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(true);

        VerifyQrResponse response = dynamicQrService.verifyStudentScan(request, studentUsername);

        assertNotNull(response);
        assertTrue(response.isVerified());
        assertEquals(sessionId, response.getSessionId());
        assertEquals(studentUsername, response.getStudentUsername());

        verify(qrNonceService, times(1)).validateAndConsumeNonce(nonce);
    }

    @Test
    @DisplayName("3. verifyStudentScan with session ID mismatch should throw InvalidQrException")
    void testVerifyStudentScan_sessionMismatch_shouldThrowInvalidQrException() {
        VerifyQrRequest request = VerifyQrRequest.builder()
                .sessionId(101L)
                .qrToken("token")
                .deviceUuid("uuid-123")
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(999L) // Mismatch
                .build();

        when(qrCryptoService.decodePayloadFromString("token")).thenReturn(payload);

        assertThrows(InvalidQrException.class, () ->
                dynamicQrService.verifyStudentScan(request, "student"));
    }

    @Test
    @DisplayName("4. verifyStudentScan with invalid HMAC signature should throw InvalidQrException")
    void testVerifyStudentScan_invalidSignature_shouldThrowInvalidQrException() {
        VerifyQrRequest request = VerifyQrRequest.builder()
                .sessionId(101L)
                .qrToken("token")
                .deviceUuid("uuid-123")
                .build();

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(101L)
                .build();

        when(qrCryptoService.decodePayloadFromString("token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(false);

        assertThrows(InvalidQrException.class, () ->
                dynamicQrService.verifyStudentScan(request, "student"));
    }

    @Test
    @DisplayName("5. verifyStudentScan with expired timestamp (>15s) should throw QrExpiredException")
    void testVerifyStudentScan_expiredTimestamp_shouldThrowQrExpiredException() {
        VerifyQrRequest request = VerifyQrRequest.builder()
                .sessionId(101L)
                .qrToken("token")
                .deviceUuid("uuid-123")
                .build();

        long expiredTime = Instant.now().getEpochSecond() - 30; // 30s ago
        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(101L)
                .timestamp(expiredTime)
                .build();

        when(qrCryptoService.decodePayloadFromString("token")).thenReturn(payload);
        when(qrCryptoService.verifySignature(payload)).thenReturn(true);

        assertThrows(QrExpiredException.class, () ->
                dynamicQrService.verifyStudentScan(request, "student"));
    }
}
