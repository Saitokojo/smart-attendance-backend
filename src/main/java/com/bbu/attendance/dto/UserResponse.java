package com.bbu.attendance.dto;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        String studentCode,
        String employeeCode,
        String roleName,
        String status
) {}