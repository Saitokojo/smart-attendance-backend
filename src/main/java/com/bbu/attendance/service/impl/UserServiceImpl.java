package com.bbu.attendance.service.impl;

import com.bbu.attendance.dto.UserRequest;
import com.bbu.attendance.dto.UserResponse;
import com.bbu.attendance.entity.Role;
import com.bbu.attendance.entity.User;
import com.bbu.attendance.repository.RoleRepository;
import com.bbu.attendance.repository.UserRepository;
import com.bbu.attendance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Requires a PasswordEncoder bean in your SecurityConfig

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already in use");
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .studentCode(request.studentCode())
                .employeeCode(request.employeeCode())
                .passwordHash(passwordEncoder.encode(request.password())) // Hash the password
                .role(role)
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    // Helper method to convert Entity to DTO
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getStudentCode(),
                user.getEmployeeCode(),
                user.getRole().getName(),
                user.getStatus()
        );
    }
}