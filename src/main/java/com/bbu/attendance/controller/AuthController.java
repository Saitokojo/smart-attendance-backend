package com.bbu.attendance.controller;

import com.bbu.attendance.dto.ApiResponse;
import com.bbu.attendance.dto.AuthResponse;
import com.bbu.attendance.dto.LoginRequest;
import com.bbu.attendance.dto.RegisterRequest;
import com.bbu.attendance.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("ចុះឈ្មោះគណនីបានជោគជ័យ!", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("ចូលប្រើប្រាស់ប្រព័ន្ធបានជោគជ័យ!", response));
    }
}