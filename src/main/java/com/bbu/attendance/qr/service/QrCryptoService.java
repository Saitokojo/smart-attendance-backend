package com.bbu.attendance.qr.service;

import com.bbu.attendance.qr.dto.DynamicQrPayload;

public interface QrCryptoService {

    /**
     * Generates an HMAC-SHA256 digital signature from QR session attributes.
     */
    String generateSignature(Long sessionId, String courseCode, long timestamp, String nonce);

    /**
     * Verifies the cryptographic signature of the payload using constant-time comparison.
     */
    boolean verifySignature(DynamicQrPayload payload);

    /**
     * Serializes and encodes the payload into a URL-safe Base64 string for QR code generation.
     */
    String encodePayloadToString(DynamicQrPayload payload);

    /**
     * Decodes and deserializes a URL-safe Base64 token back into a DynamicQrPayload object.
     * Throws InvalidQrException if token is corrupted or malformed.
     */
    DynamicQrPayload decodePayloadFromString(String token);
}
