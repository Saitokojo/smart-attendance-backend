package com.bbu.attendance.room.controller;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រិល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/controller/RoomController.java
 *
 * ការណែនាំ៖
 * - បន្ថែម Annotations: @RestController, @RequestMapping("/api/rooms"), @RequiredArgsConstructor
 * - Endpoints: POST /api/rooms (ADMIN), GET /api/rooms (Authenticated), GET /api/rooms/{id}, PUT /api/rooms/{id}
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
import com.bbu.attendance.dto.ApiResponse;
import com.bbu.attendance.room.dto.RoomRequest;
import com.bbu.attendance.room.dto.RoomResponse;
import com.bbu.attendance.room.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("បង្កើតបន្ទប់រៀនបានជោគជ័យ!", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(ApiResponse.success("ទាញយកបញ្ជីបន្ទប់ទាំងអស់បានជោគជ័យ!", rooms));
    }

    @GetMapping("/{roomCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomByCode(@PathVariable String roomCode) {
        RoomResponse room = roomService.getRoomByCode(roomCode);
        return ResponseEntity.ok(ApiResponse.success("ស្វែងរកបន្ទប់បានជោគជ័យ!", room));
    }
}
