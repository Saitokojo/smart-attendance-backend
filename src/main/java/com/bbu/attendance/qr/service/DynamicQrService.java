package com.bbu.attendance.qr.service;

import com.bbu.attendance.qr.dto.GenerateQrResponse;
import com.bbu.attendance.qr.dto.VerifyQrRequest;
import com.bbu.attendance.qr.dto.VerifyQrResponse;

/**
 * Interface គ្រប់គ្រងដំណើរការ Dynamic QR Generation & Verification Orchestration
 * សុពលភាពកូដ ១៥ វិនាទី (Rolling Refresh)
 */
public interface DynamicQrService {

    /**
     * Generates a new cryptographically signed dynamic QR token for the specified classroom session.
     * Registers the random nonce into Redis with 15 seconds TTL.
     */
    GenerateQrResponse generateRollingQr(Long sessionId);

    /**
     * Verifies student's scanned QR token against cryptographic signature and Redis nonce.
     */
    VerifyQrResponse verifyStudentScan(VerifyQrRequest request, String studentUsername);
}
