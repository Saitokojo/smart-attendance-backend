package com.bbu.attendance.service;

import com.bbu.attendance.dto.RoleRequest;
import com.bbu.attendance.dto.RoleResponse;
import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    List<RoleResponse> getAllRoles();
}