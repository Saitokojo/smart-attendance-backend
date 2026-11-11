package com.bbu.attendance.room.service.impl;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រិល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/service/impl/RoomServiceImpl.java
 *
 * ការណែនាំ៖
 * - បន្ថែម Annotations: @Service, @RequiredArgsConstructor, @Slf4j
 * - អនុវត្តន៍ Interface: implements RoomService
 * - បន្ថែម @Transactional លើ Write operations និង @Transactional(readOnly = true) លើ Read operations
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
import com.bbu.attendance.room.dto.RoomRequest;
import com.bbu.attendance.room.dto.RoomResponse;
import com.bbu.attendance.room.entity.Room;
import com.bbu.attendance.room.repository.RoomRepository;
import com.bbu.attendance.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomCode(request.roomCode())) {
            throw new RuntimeException("បន្ទប់លេខ " + request.roomCode() + " មានរួចហើយក្នុងប្រព័ន្ធ!");
        }

        Room room = Room.builder()
                .roomCode(request.roomCode().toUpperCase().trim())
                .buildingName(request.buildingName().trim())
                .floorNumber(request.floorNumber())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .geofenceRadius(request.geofenceRadius() != null ? request.geofenceRadius() : new BigDecimal("40.00"))
                .status("ACTIVE")
                .build();

        Room saved = roomRepository.save(room);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomByCode(String roomCode) {
        Room room = roomRepository.findByRoomCode(roomCode.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException("រកមិនឃើញបន្ទប់លេខ " + roomCode + " ឡើយ!"));
        return mapToResponse(room);
    }

    private RoomResponse mapToResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomCode(),
                room.getBuildingName(),
                room.getFloorNumber(),
                room.getLatitude(),
                room.getLongitude(),
                room.getGeofenceRadius(),
                room.getStatus()
        );
    }
}
