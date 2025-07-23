package com.example.ridehailing.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Fare calculation data class
 */
//data class FareCalculation(
//    val baseFare: Double,
//    val distanceFare: Double,
//    val demandMultiplier: Double,
//    val trafficMultiplier: Double = 1.0,
//    val totalFare: Double,
//    val distance: Double,
//    val estimatedDuration: Int
//) {
//
//    companion object {
//        const val BASE_FARE = 2.5
//        const val PER_KM_RATE = 1.0
//        const val PEAK_HOUR_MULTIPLIER = 1.5
//        const val HEAVY_TRAFFIC_MULTIPLIER = 1.3
//        const val NORMAL_MULTIPLIER = 1.0
//    }
//}




data class FareCalculation(
    val baseFare: Double,
    val distanceFare: Double,
    val demandMultiplier: Double,
    val trafficMultiplier: Double = 1.0,
    val totalFare: Double,
    val distance: Double,
    val estimatedDuration: Int // in minutes
) {

    companion object {
        // Nigerian Naira pricing (₦)
        const val BASE_FARE = 500.0 // ₦500 base fare
        const val PER_KM_RATE = 100.0 // ₦100 per kilometer
        const val PEAK_HOUR_MULTIPLIER = 1.5 // 1.5x during peak hours
        const val HEAVY_TRAFFIC_MULTIPLIER = 1.4 // 1.4x for heavy traffic
        const val NORMAL_MULTIPLIER = 1.0 // Normal conditions

        // Time-based multipliers
        const val LATE_NIGHT_MULTIPLIER = 1.2 // 1.2x for late night rides
        const val WEEKEND_MULTIPLIER = 1.1 // 1.1x for weekend rides

        // Minimum and maximum fare limits
        const val MIN_FARE = 300.0 // Minimum ₦300
        const val MAX_FARE = 50000.0 // Maximum ₦50,000
    }

    fun getFormattedTotalFare(): String {
        return "₦${String.format("%.0f", totalFare)}"
    }

    fun getFormattedBaseFare(): String {
        return "₦${String.format("%.0f", baseFare)}"
    }

    fun getFormattedDistanceFare(): String {
        return "₦${String.format("%.0f", distanceFare)}"
    }

    fun getFormattedDistance(): String {
        return "${String.format("%.1f", distance)} km"
    }

    fun getFormattedDuration(): String {
        val hours = estimatedDuration / 60
        val minutes = estimatedDuration % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }

    fun isValidFare(): Boolean {
        return totalFare in MIN_FARE..MAX_FARE
    }
}