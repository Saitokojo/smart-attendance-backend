package com.bbu.attendance.room.repository;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រិល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/repository/RoomRepository.java
 *
 * ការណែនាំ៖
 * - Extend: JpaRepository<Room, Long>
 * - បន្ថែម Methods: findByRoomCode(String roomCode), existsByRoomCode(String roomCode), findByIsActiveTrue()
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
public interface RoomRepository {
    // TODO [ស្រិល ម៉េងទី]: សរសេរ JPA Queries សម្រាប់ Room នៅទីនេះ
}
