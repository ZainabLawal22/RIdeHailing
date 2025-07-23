package com.example.ridehailing.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import android.location.Location

object LocationUtils {

    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in kilometers
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    /**
     * Calculate distance using Android Location class
     * Returns distance in kilometers
     */
    fun calculateDistanceAndroid(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return (results[0] / 1000.0) // Convert meters to kilometers
    }

    /**
     * Format coordinates for API calls
     */
    fun formatCoordinates(latitude: Double, longitude: Double): String {
        return "$latitude,$longitude"
    }

    /**
     * Parse coordinates from string format
     */
    fun parseCoordinates(coordinates: String): Pair<Double, Double> {
        val parts = coordinates.split(",")
        return Pair(parts[0].toDouble(), parts[1].toDouble())
    }

    /**
     * Check if coordinates are valid
     */
    fun areValidCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }
}