package com.bbu.attendance.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records", indexes = {
    @Index(name = "idx_att_session_student", columnList = "session_id, student_id", unique = true),
    @Index(name = "idx_att_session", columnList = "session_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "student_username", nullable = false)
    private String studentUsername;

    @Column(name = "device_uuid", nullable = false)
    private String deviceUuid;

    @Column(name = "student_latitude", precision = 11, scale = 8)
    private BigDecimal studentLatitude;

    @Column(name = "student_longitude", precision = 11, scale = 8)
    private BigDecimal studentLongitude;

    @Column(name = "distance_meters")
    private Double distanceMeters;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "verification_method", nullable = false)
    private String verificationMethod; // "MULTI_FACTOR_DYNAMIC_QR_GPS_DEVICE"

    @Column(name = "status", nullable = false)
    private String status; // "PRESENT", "LATE"

    @CreationTimestamp
    @Column(name = "verified_at", nullable = false, updatable = false)
    private LocalDateTime verifiedAt;
}