package com.bbu.attendance.controller;

import com.bbu.attendance.dto.RoleRequest;
import com.bbu.attendance.dto.RoleResponse;
import com.bbu.attendance.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // POST /api/roles
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
        RoleResponse createdRole = roleService.createRole(request);
        return ResponseEntity.ok(createdRole);
    }

    // GET /api/roles
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}