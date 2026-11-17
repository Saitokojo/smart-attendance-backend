package com.bbu.attendance.common.util;

import java.math.BigDecimal;

/**
 * Utility class for Geographic Distance Calculations using the Haversine Formula.
 * Implements strict Geofence boundary checking (Default radius: 40.0 meters).
 */
public final class GeoUtils {

    private static final double EARTH_RADIUS_METERS = 6371000.0; // Mean Earth radius in meters (WGS 84)

    private GeoUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Calculates the great-circle distance between two points on Earth using the Haversine formula.
     *
     * @param lat1 Latitude of point 1 (in degrees)
     * @param lon1 Longitude of point 1 (in degrees)
     * @param lat2 Latitude of point 2 (in degrees)
     * @param lon2 Longitude of point 2 (in degrees)
     * @return Distance in meters between the two points
     * @throws IllegalArgumentException if any coordinate is null
     */
    public static double calculateDistanceMeters(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new IllegalArgumentException("GPS Coordinates cannot be null");
        }

        double phi1 = Math.toRadians(lat1.doubleValue());
        double phi2 = Math.toRadians(lat2.doubleValue());
        double deltaPhi = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLambda = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double sinDeltaPhi2 = Math.sin(deltaPhi / 2.0);
        double sinDeltaLambda2 = Math.sin(deltaLambda / 2.0);

        double a = sinDeltaPhi2 * sinDeltaPhi2
                + Math.cos(phi1) * Math.cos(phi2) * sinDeltaLambda2 * sinDeltaLambda2;

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return EARTH_RADIUS_METERS * c;
    }

    /**
     * Verifies if a student's GPS location is within the classroom's allowed geofence boundary.
     *
     * @param studentLat   Student's detected latitude
     * @param studentLon   Student's detected longitude
     * @param roomLat      Classroom's designated latitude
     * @param roomLon      Classroom's designated longitude
     * @param radiusMeters Maximum allowed distance in meters (defaults to 40.0m if null)
     * @return true if student is inside or exactly on the geofence perimeter; false otherwise
     */
    public static boolean isWithinGeofence(BigDecimal studentLat, BigDecimal studentLon,
                                          BigDecimal roomLat, BigDecimal roomLon,
                                          BigDecimal radiusMeters) {
        double allowedRadius = (radiusMeters != null) ? radiusMeters.doubleValue() : 40.0;
        double actualDistance = calculateDistanceMeters(studentLat, studentLon, roomLat, roomLon);
        return actualDistance <= allowedRadius;
    }
}
