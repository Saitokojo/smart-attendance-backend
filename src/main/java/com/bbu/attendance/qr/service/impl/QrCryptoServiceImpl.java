package com.bbu.attendance.qr.service.impl;

import com.bbu.attendance.common.exception.InvalidQrException;
import com.bbu.attendance.qr.dto.DynamicQrPayload;
import com.bbu.attendance.qr.service.QrCryptoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
@Service
public class QrCryptoServiceImpl implements QrCryptoService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final String secretKey;
    private final ObjectMapper objectMapper;

    public QrCryptoServiceImpl(
            @Value("${app.qr.secret-key}") String secretKey,
            ObjectMapper objectMapper) {
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateSignature(Long sessionId, String courseCode, long timestamp, String nonce) {
        try {
            String data = sessionId + ":" + courseCode + ":" + timestamp + ":" + nonce;
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
        } catch (Exception e) {
            log.error("Failed to generate HMAC-SHA256 signature", e);
            throw new RuntimeException("HMAC computation error", e);
        }
    }

    @Override
    public boolean verifySignature(DynamicQrPayload payload) {
        if (payload == null || payload.getSignature() == null) {
            return false;
        }
        String expected = generateSignature(
                payload.getSessionId(),
                payload.getCourseCode(),
                payload.getTimestamp(),
                payload.getNonce()
        );
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                payload.getSignature().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String encodePayloadToString(DynamicQrPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Payload encoding error", e);
        }
    }

    @Override
    public DynamicQrPayload decodePayloadFromString(String token) {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
            return objectMapper.readValue(decodedBytes, DynamicQrPayload.class);
        } catch (Exception e) {
            log.warn("Invalid QR Token format: {}", e.getMessage());
            throw new InvalidQrException("Invalid or corrupted Dynamic QR Token");
        }
    }
}
