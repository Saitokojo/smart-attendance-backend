package com.bbu.attendance.room.dto;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រិល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/dto/RoomResponse.java
 *
 * ការណែនាំ៖
 * - បង្កើត Java Record សម្រាប់បញ្ជូនទិន្នន័យបន្ទប់ និងកូអរដោនេ Geofence ចេញទៅខាងក្រៅ
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
import java.math.BigDecimal;

public record RoomResponse(
    Long id,
    String roomCode,
    String buildingName,
    Integer floorNumber,
    BigDecimal latitude,
    BigDecimal longitude,
    BigDecimal geofenceRadius,
    String status
) {}
