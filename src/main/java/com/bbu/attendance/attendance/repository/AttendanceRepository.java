package com.bbu.attendance.attendance.repository;

import com.bbu.attendance.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    boolean existsBySessionIdAndStudentId(Long sessionId, Long studentId);
    List<AttendanceRecord> findBySessionIdOrderByVerifiedAtDesc(Long sessionId);
    long countBySessionId(Long sessionId);
}