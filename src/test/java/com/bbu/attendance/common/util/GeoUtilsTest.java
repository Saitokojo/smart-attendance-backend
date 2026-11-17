package com.bbu.attendance.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests for GeoUtils (Haversine Formula & 40m Geofence Guard)")
class GeoUtilsTest {

    // Reference Point: Build Bright University (BBU) Phnom Penh Campus - Room LAB-302
    private static final BigDecimal BBU_ROOM_LAT = new BigDecimal("11.556400");
    private static final BigDecimal BBU_ROOM_LON = new BigDecimal("104.928200");

    @Test
    @DisplayName("1. calculateDistanceMeters for identical coordinates should return 0.0m")
    void testCalculateDistance_identicalCoordinates_shouldReturnZero() {
        double distance = GeoUtils.calculateDistanceMeters(
                BBU_ROOM_LAT, BBU_ROOM_LON,
                BBU_ROOM_LAT, BBU_ROOM_LON
        );
        assertEquals(0.0, distance, 0.0001, "Distance between identical points must be 0 meters");
    }

    @Test
    @DisplayName("2. calculateDistanceMeters for known offset (~16.68m) should calculate accurately")
    void testCalculateDistance_knownOffset_shouldBeAccurate() {
        // Offset of 0.00015 degrees latitude corresponds to approx 16.68 meters
        BigDecimal studentLat = new BigDecimal("11.556550");
        BigDecimal studentLon = new BigDecimal("104.928200");

        double distance = GeoUtils.calculateDistanceMeters(
                studentLat, studentLon,
                BBU_ROOM_LAT, BBU_ROOM_LON
        );

        assertTrue(distance > 16.0 && distance < 17.5,
                "Calculated distance should be approximately 16.68m, but was: " + distance);
    }

    @Test
    @DisplayName("3. isWithinGeofence when student is inside 40.0m radius should return TRUE")
    void testIsWithinGeofence_studentInside40m_shouldReturnTrue() {
        // Student is ~16.68 meters away from room center
        BigDecimal studentLat = new BigDecimal("11.556550");
        BigDecimal studentLon = new BigDecimal("104.928200");

        boolean withinGeofence = GeoUtils.isWithinGeofence(
                studentLat, studentLon,
                BBU_ROOM_LAT, BBU_ROOM_LON,
                new BigDecimal("40.0")
        );

        assertTrue(withinGeofence, "Student within 16.7m must be accepted inside 40m geofence");
    }

    @Test
    @DisplayName("4. isWithinGeofence when student is outside 40.0m radius (~177m) should return FALSE")
    void testIsWithinGeofence_studentOutside40m_shouldReturnFalse() {
        // Student is ~177 meters away (e.g., at coffee shop across the street)
        BigDecimal studentLat = new BigDecimal("11.558000");
        BigDecimal studentLon = new BigDecimal("104.928200");

        boolean withinGeofence = GeoUtils.isWithinGeofence(
                studentLat, studentLon,
                BBU_ROOM_LAT, BBU_ROOM_LON,
                new BigDecimal("40.0")
        );

        assertFalse(withinGeofence, "Student outside 40m must be strictly rejected by Geofence Guard");
    }

    @Test
    @DisplayName("5. isWithinGeofence with null radius should default strictly to 40.0m")
    void testIsWithinGeofence_nullRadius_shouldDefaultTo40Meters() {
        BigDecimal insideLat = new BigDecimal("11.556550");
        BigDecimal insideLon = new BigDecimal("104.928200");

        BigDecimal outsideLat = new BigDecimal("11.558000");
        BigDecimal outsideLon = new BigDecimal("104.928200");

        // Null radius should use default 40.0m
        assertTrue(GeoUtils.isWithinGeofence(insideLat, insideLon, BBU_ROOM_LAT, BBU_ROOM_LON, null),
                "Should default to 40.0m and accept student at ~16.7m");

        assertFalse(GeoUtils.isWithinGeofence(outsideLat, outsideLon, BBU_ROOM_LAT, BBU_ROOM_LON, null),
                "Should default to 40.0m and reject student at ~177m");
    }

    @Test
    @DisplayName("6. calculateDistanceMeters should throw IllegalArgumentException when any coordinate is null")
    void testNullCoordinates_shouldThrowIllegalArgumentException() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                GeoUtils.calculateDistanceMeters(null, BBU_ROOM_LON, BBU_ROOM_LAT, BBU_ROOM_LON));
        assertEquals("GPS Coordinates cannot be null", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                GeoUtils.calculateDistanceMeters(BBU_ROOM_LAT, null, BBU_ROOM_LAT, BBU_ROOM_LON));
        assertEquals("GPS Coordinates cannot be null", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () ->
                GeoUtils.calculateDistanceMeters(BBU_ROOM_LAT, BBU_ROOM_LON, null, BBU_ROOM_LON));
        assertEquals("GPS Coordinates cannot be null", ex3.getMessage());

        IllegalArgumentException ex4 = assertThrows(IllegalArgumentException.class, () ->
                GeoUtils.calculateDistanceMeters(BBU_ROOM_LAT, BBU_ROOM_LON, BBU_ROOM_LAT, null));
        assertEquals("GPS Coordinates cannot be null", ex4.getMessage());
    }
}
