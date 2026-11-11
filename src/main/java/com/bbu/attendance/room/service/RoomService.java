package com.bbu.attendance.room.service;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រិល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/service/RoomService.java
 *
 * ការណែនាំ៖
 * - បង្កើត Interface Method signatures: createRoom, getAllActiveRooms, getRoomById, updateRoom, deactivateRoom
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
import com.bbu.attendance.room.dto.RoomRequest;
import com.bbu.attendance.room.dto.RoomResponse;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(RoomRequest request);
    List<RoomResponse> getAllRooms();
    RoomResponse getRoomByCode(String roomCode);
}
