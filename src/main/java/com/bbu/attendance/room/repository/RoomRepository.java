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
import com.bbu.attendance.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomCode(String roomCode);
    boolean existsByRoomCode(String roomCode);
}
