package com.bbu.attendance.qr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO សម្រាប់ផ្តល់ទិន្នន័យចាក់លើផ្ទាំង Teacher Web Projector (Base64 QR Image + Countdown Seconds)
 * ទទួលខុសត្រូវដោយ៖ និស្សិត សេង ស៊ីមផា (ថ្ងៃសុក្រ)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectorQrResponse {
    private Long sessionId;
    private String className;
    private String roomCode;
    private String qrBase64Image;
    private String qrToken;
    private int countdownSeconds;
    private long refreshIntervalMs;
}
