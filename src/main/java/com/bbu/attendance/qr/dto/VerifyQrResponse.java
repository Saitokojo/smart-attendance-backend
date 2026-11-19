package com.bbu.attendance.qr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyQrResponse {
    private boolean verified;
    private Long sessionId;
    private String studentUsername;
    private LocalDateTime verifiedAt;
    private String message;
}