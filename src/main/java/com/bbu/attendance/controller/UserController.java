package com.bbu.attendance.controller;

import com.bbu.attendance.dto.UserRequest;
import com.bbu.attendance.dto.UserResponse;
import com.bbu.attendance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/users
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return ResponseEntity.ok(createdUser);
    }

    // GET /api/users/{email}
    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}