package com.example.ridehailing.domain.model

/**
 * Fare calculation data class
 */

data class FareCalculation(
    val baseFare: Double,
    val distanceFare: Double,
    val demandMultiplier: Double,
    val trafficMultiplier: Double = 1.0,
    val totalFare: Double,
    val distance: Double,
    val estimatedDuration: Int
) {

    companion object {

        const val BASE_FARE = 500.0
        const val PER_KM_RATE = 100.0
        const val PEAK_HOUR_MULTIPLIER = 1.5
        const val HEAVY_TRAFFIC_MULTIPLIER = 1.4
        const val NORMAL_MULTIPLIER = 1.0
    }

}