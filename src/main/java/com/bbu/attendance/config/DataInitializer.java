package com.bbu.attendance.config;

import com.bbu.attendance.entity.Role;
import com.bbu.attendance.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // បង្កើត Roles លំនាំដើមស្វ័យប្រវត្តិពេល Run ដំបូង បើមិនទាន់មានក្នុង Database
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
    }
}
