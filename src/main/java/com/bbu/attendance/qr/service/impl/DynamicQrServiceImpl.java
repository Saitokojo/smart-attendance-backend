package com.bbu.attendance.qr.service.impl;

import com.bbu.attendance.common.exception.InvalidQrException;
import com.bbu.attendance.common.exception.QrExpiredException;
import com.bbu.attendance.qr.dto.DynamicQrPayload;
import com.bbu.attendance.qr.dto.GenerateQrResponse;
import com.bbu.attendance.qr.dto.VerifyQrRequest;
import com.bbu.attendance.qr.dto.VerifyQrResponse;
import com.bbu.attendance.qr.service.DynamicQrService;
import com.bbu.attendance.qr.service.QrCryptoService;
import com.bbu.attendance.qr.service.QrNonceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation សម្រាប់ DynamicQrService
 * គ្រប់គ្រងការបង្កើត Rolling QR Token រៀងរាល់ ១៥ វិនាទី និងផ្ទៀងផ្ទាត់ហត្ថលេខាឌីជីថល + Nonce
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicQrServiceImpl implements DynamicQrService {

    private final QrCryptoService qrCryptoService;
    private final QrNonceService qrNonceService;

    @Value("${app.qr.validity-seconds:15}")
    private int validitySeconds;

    @Value("${app.qr.clock-skew-seconds:2}")
    private int clockSkewSeconds;

    @Override
    public GenerateQrResponse generateRollingQr(Long sessionId) {
        long now = Instant.now().getEpochSecond();
        String nonce = UUID.randomUUID().toString();
        String courseCode = "CS-" + sessionId; // Dynamic course identifier

        String signature = qrCryptoService.generateSignature(sessionId, courseCode, now, nonce);

        DynamicQrPayload payload = DynamicQrPayload.builder()
                .sessionId(sessionId)
                .courseCode(courseCode)
                .timestamp(now)
                .nonce(nonce)
                .signature(signature)
                .build();

        String token = qrCryptoService.encodePayloadToString(payload);

        // Register Nonce in Redis with exact TTL 15s
        qrNonceService.registerNonce(nonce, sessionId, validitySeconds);

        log.info("Generated Dynamic QR Token for Session {} with Nonce {}", sessionId, nonce);

        return GenerateQrResponse.builder()
                .qrToken(token)
                .sessionId(sessionId)
                .courseCode(courseCode)
                .generatedAt(now)
                .expiresInSeconds(validitySeconds)
                .nonce(nonce)
                .build();
    }

    @Override
    public VerifyQrResponse verifyStudentScan(VerifyQrRequest request, String studentUsername) {
        DynamicQrPayload payload = qrCryptoService.decodePayloadFromString(request.getQrToken());

        // 1. Check Session matching
        if (!payload.getSessionId().equals(request.getSessionId())) {
            throw new InvalidQrException("QR Code belongs to a different class session");
        }

        // 2. Cryptographic HMAC Signature Verification
        if (!qrCryptoService.verifySignature(payload)) {
            throw new InvalidQrException("Cryptographic signature check failed! QR has been tampered with.");
        }

        // 3. Timestamp Window Verification (TTL + Allowed Skew)
        long currentSec = Instant.now().getEpochSecond();
        if ((currentSec - payload.getTimestamp()) > (validitySeconds + clockSkewSeconds)) {
            throw new QrExpiredException("QR Token timestamp has expired (> 15s)");
        }

        // 4. Redis Nonce Consumption (Single-Use Anti-Replay Guard)
        qrNonceService.validateAndConsumeNonce(payload.getNonce());

        log.info("Student {} successfully verified QR for Session {}", studentUsername, request.getSessionId());

        return VerifyQrResponse.builder()
                .verified(true)
                .sessionId(request.getSessionId())
                .studentUsername(studentUsername)
                .verifiedAt(LocalDateTime.now())
                .message("Dynamic QR Code verified successfully!")
                .build();
    }
}
