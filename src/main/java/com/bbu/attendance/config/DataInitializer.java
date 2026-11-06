package com.bbu.attendance.config;

import com.bbu.attendance.entity.Role;
import com.bbu.attendance.entity.User;
import com.bbu.attendance.repository.RoleRepository;
import com.bbu.attendance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. បង្កើត Roles លំនាំដើមស្វ័យប្រវត្តិពេល Run ដំបូង បើមិនទាន់មានក្នុង Database
        List<String> defaultRoles = List.of("STUDENT", "TEACHER", "ADMIN");

        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .description("តួនាទីជា " + roleName + " ក្នុងប្រព័ន្ធវត្តមាន")
                        .build();
                roleRepository.save(role);
                System.out.println(">>> [AUTO-SEED] បានបង្កើត Role ដោយស្វ័យប្រវត្តិ: " + roleName);
            }
        }

        // 2. បង្កើតគណនី Admin ដំបូងស្វ័យប្រវត្តិ (Initial System Admin) បើមិនទាន់មាន
        String adminEmail = "admin@bbu.edu.kh";
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("មិនឃើញ Role ADMIN ពេល Auto-seed"));

            User adminUser = User.builder()
                    .name("System Administrator")
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode("Admin@123456"))
                    .role(adminRole)
                    .employeeCode("BBU-ADM-000")
                    .phone("012345679")
                    .status("ACTIVE")
                    .build();

            userRepository.save(adminUser);
            System.out.println(">>> [AUTO-SEED] បានបង្កើតគណនី Admin ដំបូងដោយស្វ័យប្រវត្តិ: " + adminEmail + " (Password: Admin@123456)");
        }
    }
}
