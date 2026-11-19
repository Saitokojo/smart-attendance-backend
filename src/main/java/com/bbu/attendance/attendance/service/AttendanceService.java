package com.bbu.attendance.attendance.service;

import com.bbu.attendance.attendance.dto.AttendanceCheckInRequest;
import com.bbu.attendance.attendance.dto.AttendanceCheckInResponse;
import com.bbu.attendance.attendance.entity.AttendanceRecord;

import java.util.List;

public interface AttendanceService {

    /**
     * ផ្ទៀងផ្ទាត់ និងកត់ត្រាវត្តមាននិស្សិតតាមរយៈ 10-Step Verification Pipeline
     */
    AttendanceCheckInResponse checkInStudent(
            AttendanceCheckInRequest request,
            String studentUsername,
            String clientIp
    );

    /**
     * ទាញយកបញ្ជីវត្តមាននិស្សិតទាំងអស់ក្នុង Session នីមួយៗ
     */
    List<AttendanceRecord> getSessionRoster(Long sessionId);
}