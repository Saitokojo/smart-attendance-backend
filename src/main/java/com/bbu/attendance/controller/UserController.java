package com.bbu.attendance.controller;

import com.bbu.attendance.dto.UserRequest;
import com.bbu.attendance.dto.UserResponse;
import com.bbu.attendance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/users - បង្កើត User ដោយផ្ទាល់ អនុញ្ញាតតែ ADMIN ប៉ុណ្ណោះ
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse createdUser = userService.createUser(request);
        return ResponseEntity.ok(createdUser);
    }

    // GET /api/users/{email} - ADMIN និង TEACHER អាចមើល User ទាំងអស់បាន ចំណែក STUDENT មើលបានតែ Profile ផ្ទាល់ខ្លួនប៉ុណ្ណោះ
    @GetMapping("/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or #email == authentication.name")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
