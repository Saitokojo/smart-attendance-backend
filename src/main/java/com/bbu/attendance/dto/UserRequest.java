package com.bbu.attendance.dto;

public record UserRequest(
        String name,
        String email,
        String password,
        Long roleId,
        String phone,
        String studentCode,
        String employeeCode
) {}
