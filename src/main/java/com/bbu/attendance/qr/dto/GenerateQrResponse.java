package com.bbu.attendance.qr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateQrResponse {
    private String qrToken;
    private Long sessionId;
    private String courseCode;
    private long generatedAt;
    private int expiresInSeconds;
    private String nonce;
}