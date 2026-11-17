package com.bbu.attendance.qr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicQrPayload {
    private Long sessionId;
    private String courseCode;
    private long timestamp;
    private String nonce;
    private String signature;
}
