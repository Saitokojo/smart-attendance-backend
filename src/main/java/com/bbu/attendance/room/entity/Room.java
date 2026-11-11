package com.bbu.attendance.room.entity;

/**
 * ម៉ូឌុលទី ១៖ Room & Geofence GPS Coordinates
 * សមាជិកទទួលខុសត្រូវ៖ ស្រិល ម៉េងទី (វេនថ្ងៃពុធ)
 * ទីតាំង File៖ src/main/java/com/bbu/attendance/room/entity/Room.java
 *
 * ការណែនាំ៖
 * - បន្ថែម Annotations: @Entity, @Table(name = "rooms"), @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor, @Builder
 * - បំពេញ Attributes: id, roomCode, buildingName, floorNumber, latitude, longitude, radiusMeters, isActive, createdAt, updatedAt
 * - យោងតាម៖ ជំពូកទី ៤ នៃ WEEK_2_FULL_CODE_REFERENCE.md
 */

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_rooms_room_code", columnList = "room_code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", unique = true, nullable = false, length = 50)
    private String roomCode;

    @Column(name = "building_name", nullable = false, length = 100)
    private String buildingName;

    @Column(name = "floor_number", nullable = false)
    @Builder.Default
    private Integer floorNumber = 1;

    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "geofence_radius", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal geofenceRadius = new BigDecimal("40.00");

    @Column(length = 30)
    @Builder.Default
    private String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}