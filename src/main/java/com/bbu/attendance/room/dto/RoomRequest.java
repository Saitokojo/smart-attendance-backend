package com.bbu.attendance.room.dto;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រិល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/dto/RoomRequest.java
 *
 * ការណែនាំ៖
 * - បង្កើត Java Record សម្រាប់ទទួលទិន្នន័យបង្កើត ឬកែប្រែបន្ទប់
 * - បន្ថែម Jakarta Validations: @NotBlank roomCode, @NotBlank buildingName, @NotNull floorNumber, @NotNull latitude, @NotNull longitude, @NotNull radiusMeters
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RoomRequest(
    @NotBlank(message = "លេខសម្គាល់បន្ទប់មិនអាចទទេបានទេ (Room code is required)")
    String roomCode,

    @NotBlank(message = "ឈ្មោះអាគារមិនអាចទទេបានទេ (Building name is required)")
    String buildingName,

    @NotNull(message = "ជាន់មិនអាចទទេបានទេ (Floor number is required)")
    Integer floorNumber,

    @NotNull(message = "Latitude មិនអាចទទេបានទេ")
    BigDecimal latitude,

    @NotNull(message = "Longitude មិនអាចទទេបានទេ")
    BigDecimal longitude,

    BigDecimal geofenceRadius
) {}
