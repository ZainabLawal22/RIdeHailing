package com.example.ridehailing.domain.model

import java.time.LocalDateTime

/**
 * Ride data class
 */

data class Ride(
    val id: Long = 0,
    val pickupLocation: Location,
    val destinationLocation: Location,
    val fareEstimate: FareCalculation,
    val driver: Driver? = null,
    val status: RideStatus,
    val requestTime: LocalDateTime,
    val completionTime: LocalDateTime? = null,
    val notes: String = ""
)

enum class RideStatus {
    REQUESTED,
    CONFIRMED,
    DRIVER_ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

}