package com.bbu.attendance.qr;

import com.bbu.attendance.common.exception.InvalidQrException;
import com.bbu.attendance.qr.dto.DynamicQrPayload;
import com.bbu.attendance.qr.service.QrCryptoService;
import com.bbu.attendance.qr.service.impl.QrCryptoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for QrCryptoService (HMAC-SHA256 & Token Serialization)")
class QrCryptoServiceTest {

    private static final String TEST_SECRET_KEY = "TEST_SECRET_KEY_BBU_SMART_ATTENDANCE_2026_VERY_SECURE";
    private QrCryptoService qrCryptoService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        qrCryptoService = new QrCryptoServiceImpl(TEST_SECRET_KEY, objectMapper);
    }

    @Test
    @DisplayName("1. generateSignature should return a valid URL-safe Base64 HMAC signature without padding")
    void testGenerateSignature_shouldReturnValidBase64UrlSafe() {
        Long sessionId = 101L;
        String courseCode = "CS-301";
        long timestamp = 1700000000000L;
        String nonce = UUID.randomUUID().toString();

        String signature = qrCryptoService.generateSignature(sessionId, courseCode, timestamp, nonce);

        assertNotNull(signature, "Signature must not be null");
        assertFalse(signature.isBlank(), "Signature must not be blank");
        assertFalse(signature.contains("+"), "URL-safe Base64 must not contain '+'");
        assertFalse(signature.contains("/"), "URL-safe Base64 must not contain '/'");
        assertFalse(signature.contains("="), "Signature without padding must not contain '='");
    }

    @Test
    @DisplayName("2. verifySignature with untampered payload should return true")
    void testVerifySignature_withValidPayload_shouldReturnTrue() {
        Long sessionId = 42L;
        String courseCode = "SWE-401";
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();

        String signature = qrCryptoService.generateSignature(sessionId, courseCode, timestamp, nonce);

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode(courseCode)
                .timestamp(timestamp)
                .nonce(nonce)
                .signature(signature)
                .build();

        boolean isValid = qrCryptoService.verifySignature(payload);
        assertTrue(isValid, "Cryptographic verification must succeed for untampered payload");
    }

    @Test
    @DisplayName("3. verifySignature with tampered signature string should return false")
    void testVerifySignature_withTamperedSignature_shouldReturnFalse() {
        Long sessionId = 42L;
        String courseCode = "SWE-401";
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();

        String originalSignature = qrCryptoService.generateSignature(sessionId, courseCode, timestamp, nonce);
        String tamperedSignature = originalSignature.substring(0, originalSignature.length() - 2) + "ZZ";

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode(courseCode)
                .timestamp(timestamp)
                .nonce(nonce)
                .signature(tamperedSignature)
                .build();

        boolean isValid = qrCryptoService.verifySignature(payload);
        assertFalse(isValid, "Verification must reject tampered signature");
    }

    @Test
    @DisplayName("4. verifySignature with tampered session ID should return false")
    void testVerifySignature_withTamperedSessionId_shouldReturnFalse() {
        Long sessionId = 42L;
        String courseCode = "SWE-401";
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();

        String signature = qrCryptoService.generateSignature(sessionId, courseCode, timestamp, nonce);

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(999L) // Tampered ID
                .courseCode(courseCode)
                .timestamp(timestamp)
                .nonce(nonce)
                .signature(signature)
                .build();

        boolean isValid = qrCryptoService.verifySignature(payload);
        assertFalse(isValid, "Verification must fail when session ID is modified");
    }

    @Test
    @DisplayName("5. verifySignature with tampered courseCode should return false")
    void testVerifySignature_withTamperedCourseCode_shouldReturnFalse() {
        Long sessionId = 42L;
        String courseCode = "SWE-401";
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();

        String signature = qrCryptoService.generateSignature(sessionId, courseCode, timestamp, nonce);

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode("SWE-999") // Tampered course
                .timestamp(timestamp)
                .nonce(nonce)
                .signature(signature)
                .build();

        boolean isValid = qrCryptoService.verifySignature(payload);
        assertFalse(isValid, "Verification must fail when course code is modified");
    }

    @Test
    @DisplayName("6. verifySignature with tampered timestamp should return false")
    void testVerifySignature_withTamperedTimestamp_shouldReturnFalse() {
        Long sessionId = 42L;
        String courseCode = "SWE-401";
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString();

        String signature = qrCryptoService.generateSignature(sessionId, courseCode, timestamp, nonce);

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode(courseCode)
                .timestamp(timestamp + 50000L) // Tampered timestamp
                .nonce(nonce)
                .signature(signature)
                .build();

        boolean isValid = qrCryptoService.verifySignature(payload);
        assertFalse(isValid, "Verification must fail when timestamp is modified");
    }

    @Test
    @DisplayName("7. verifySignature with null payload or signature should return false")
    void testVerifySignature_withNullValues_shouldReturnFalse() {
        assertFalse(qrCryptoService.verifySignature(null), "Null payload should return false");

        DynamicQrPayload payloadWithNullSig = DynamicQrPayload.builder()
                .sessionId(1L)
                .courseCode("CS-101")
                .timestamp(1000L)
                .nonce("nonce")
                .signature(null)
                .build();

        assertFalse(qrCryptoService.verifySignature(payloadWithNullSig), "Null signature should return false");
    }

    @Test
    @DisplayName("8. encodePayloadToString and decodePayloadFromString should be lossless round-trip")
    void testEncodeAndDecodePayload_roundTrip_shouldMatch() {
        Long sessionId = 77L;
        String courseCode = "ITE-402";
        long timestamp = 1731800000000L;
        String nonce = UUID.randomUUID().toString();
        String signature = qrCryptoService.generateSignature(sessionId, courseCode, timestamp, nonce);

        DynamicQrPayload original = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode(courseCode)
                .timestamp(timestamp)
                .nonce(nonce)
                .signature(signature)
                .build();

        String token = qrCryptoService.encodePayloadToString(original);
        assertNotNull(token);
        assertFalse(token.isBlank());

        DynamicQrPayload decoded = qrCryptoService.decodePayloadFromString(token);
        assertEquals(original.getSessionId(), decoded.getSessionId());
        assertEquals(original.getCourseCode(), decoded.getCourseCode());
        assertEquals(original.getTimestamp(), decoded.getTimestamp());
        assertEquals(original.getNonce(), decoded.getNonce());
        assertEquals(original.getSignature(), decoded.getSignature());
    }

    @Test
    @DisplayName("9. decodePayloadFromString with invalid or corrupted string should throw InvalidQrException")
    void testDecodePayloadFromString_withCorruptedToken_shouldThrowInvalidQrException() {
        String invalidToken = "NotAValidBase64EncodedPayload!!@@##";

        assertThrows(InvalidQrException.class, () -> {
            qrCryptoService.decodePayloadFromString(invalidToken);
        }, "Corrupted QR token must trigger InvalidQrException");
    }
}
