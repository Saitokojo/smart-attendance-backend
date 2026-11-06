package com.bbu.attendance.service.impl;

import com.bbu.attendance.dto.AuthResponse;
import com.bbu.attendance.dto.LoginRequest;
import com.bbu.attendance.dto.RegisterRequest;
import com.bbu.attendance.entity.Role;
import com.bbu.attendance.entity.User;
import com.bbu.attendance.repository.RoleRepository;
import com.bbu.attendance.repository.UserRepository;
import com.bbu.attendance.security.JwtService;
import com.bbu.attendance.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. ពិនិត្យ Email ស្ទួន
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email នេះត្រូវបានចុះឈ្មោះរួចហើយ! (Email already in use)");
        }

        // 2. ពិនិត្យ Student Code ស្ទួន (បើមាន)
        if (request.getStudentCode() != null && !request.getStudentCode().isBlank()) {
            if (userRepository.existsByStudentCode(request.getStudentCode())) {
                throw new IllegalArgumentException("លេខកូដសម្គាល់សិស្សនេះមានរួចហើយ! (Student code already exists)");
            }
        }

        // 3. ស្វែងរក Role និងការពារ Privilege Escalation
        String roleName = request.getRole().toUpperCase().trim();
        if ("ADMIN".equals(roleName)) {
            throw new IllegalArgumentException("មិនអនុញ្ញាតឱ្យចុះឈ្មោះជា Admin តាមរយៈ Public Register ឡើយ! (Cannot register as ADMIN)");
        }
        if (!"STUDENT".equals(roleName) && !"TEACHER".equals(roleName)) {
            throw new IllegalArgumentException("Role មិនត្រឹមត្រូវ! អនុញ្ញាតតែ STUDENT ឬ TEACHER ប៉ុណ្ណោះ");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("មិនស្គាល់ Role: " + roleName + " (ត្រូវប្រើ STUDENT ឬ TEACHER)"));

        // 4. បង្កើត User ថ្មី និង Hash Password ជាមួយ BCrypt
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .studentCode(request.getStudentCode())
                .phone(request.getPhone())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(user);

        // 5. បង្កើត JWT Token
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().getName())
                .studentCode(savedUser.getStudentCode())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        // 1. ផ្ទៀងផ្ទាត់ Email និង Password តាមរយៈ AuthenticationManager (រួមទាំង Disabled check)
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email ឬ Password មិនត្រឹមត្រូវឡើយ! (Invalid email or password)");
        }

        // 2. ទាញយក User ពី Database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("រកមិនឃើញគណនីនេះក្នុងប្រព័ន្ធទេ!"));

        // 3. បង្កើត JWT Token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .studentCode(user.getStudentCode())
                .build();
    }
}
