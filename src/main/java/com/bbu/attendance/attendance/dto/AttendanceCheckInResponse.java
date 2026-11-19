package com.bbu.attendance.attendance.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCheckInResponse {

    private Long attendanceId;
    private Long sessionId;
    private String studentUsername;
    private String status;
    private double distanceMeters;
    private String verificationSummary;
    private LocalDateTime verifiedAt;
}