package com.bbu.attendance.service.impl;

import com.bbu.attendance.dto.RoleRequest;
import com.bbu.attendance.dto.RoleResponse;
import com.bbu.attendance.entity.Role;
import com.bbu.attendance.repository.RoleRepository;
import com.bbu.attendance.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new RuntimeException("Role name already exists");
        }

        Role role = Role.builder()
                .name(request.name())
                .description(request.description())
                .build();

        Role savedRole = roleRepository.save(role);
        return mapToResponse(savedRole);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RoleResponse mapToResponse(Role role) {
        return new RoleResponse(
            role.getId(),
            role.getName(),
            role.getDescription()
        );
    }
}