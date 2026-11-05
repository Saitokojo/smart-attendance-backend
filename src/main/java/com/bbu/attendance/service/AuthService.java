package com.bbu.attendance.service;

import com.bbu.attendance.dto.AuthResponse;
import com.bbu.attendance.dto.LoginRequest;
import com.bbu.attendance.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}