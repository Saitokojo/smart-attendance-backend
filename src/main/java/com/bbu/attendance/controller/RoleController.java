package com.bbu.attendance.controller;

import com.bbu.attendance.dto.RoleRequest;
import com.bbu.attendance.dto.RoleResponse;
import com.bbu.attendance.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // POST /api/roles - សម្រាប់តែ ADMIN ប៉ុណ្ណោះ
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        RoleResponse createdRole = roleService.createRole(request);
        return ResponseEntity.ok(createdRole);
    }

    // GET /api/roles - សម្រាប់ ADMIN និង TEACHER
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
