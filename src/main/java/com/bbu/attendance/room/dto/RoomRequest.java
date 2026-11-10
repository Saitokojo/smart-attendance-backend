package com.bbu.attendance.room.dto;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រ៊្រីល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/dto/RoomRequest.java
 *
 * ការណែនាំ៖
 * - បង្កើត Java Record សម្រាប់ទទួលទិន្នន័យបង្កើត ឬកែប្រែបន្ទប់
 * - បន្ថែម Jakarta Validations: @NotBlank roomCode, @NotBlank buildingName, @NotNull floorNumber, @NotNull latitude, @NotNull longitude, @NotNull radiusMeters
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */
public record RoomRequest() {
    // TODO [ស្រ៊្រីល ម៉េងទី]: បំពេញ Record Parameters សម្រាប់ Room Request នៅទីនេះ
}
