package com.bbu.attendance.service;

import com.bbu.attendance.dto.UserRequest;
import com.bbu.attendance.dto.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserByEmail(String email);
}