package com.example.ridehailing.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

/**
 * Ride data class
 */

//data class Ride(
//    val id: Long = 0,
//    val pickupLocation: Location,
//    val destinationLocation: Location,
//    val fareEstimate: FareCalculation,
//    val driver: Driver? = null,
//    val status: RideStatus,
//    val requestTime: LocalDateTime,
//    val completionTime: LocalDateTime? = null
//)
//
//enum class RideStatus {
//    REQUESTED,
//    CONFIRMED,
//    DRIVER_ASSIGNED,
//    IN_PROGRESS,
//    COMPLETED,
//    CANCELLED
//}



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
) {

    fun getStatusDisplay(): String {
        return when (status) {
            RideStatus.REQUESTED -> "Ride Requested"
            RideStatus.CONFIRMED -> "Ride Confirmed"
            RideStatus.DRIVER_ASSIGNED -> "Driver Assigned"
            RideStatus.IN_PROGRESS -> "In Progress"
            RideStatus.COMPLETED -> "Completed"
            RideStatus.CANCELLED -> "Cancelled"
        }
    }

    fun isActive(): Boolean {
        return status in listOf(
            RideStatus.REQUESTED,
            RideStatus.CONFIRMED,
            RideStatus.DRIVER_ASSIGNED,
            RideStatus.IN_PROGRESS
        )
    }

    fun isCompleted(): Boolean {
        return status == RideStatus.COMPLETED
    }

    fun isCancelled(): Boolean {
        return status == RideStatus.CANCELLED
    }

    fun getDurationDisplay(): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (completionTime != null) {
                    val duration = java.time.Duration.between(requestTime, completionTime)
                    val minutes = duration.toMinutes()
                    if (minutes < 60) {
                        "${minutes} min"
                    } else {
                        val hours = minutes / 60
                        val remainingMinutes = minutes % 60
                        "${hours}h ${remainingMinutes}m"
                    }
                } else {
                    "Ongoing"
                }
            } else {
                "Duration not available"
            }
        } catch (e: Exception) {
            "Duration not available"
        }
    }

    fun getRouteDisplay(): String {
        val pickup = pickupLocation.address.ifEmpty { "Pickup Location" }
        val destination = destinationLocation.address.ifEmpty { "Destination" }
        return "$pickup → $destination"
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun createNewRide(
            pickupLocation: Location,
            destinationLocation: Location,
            fareEstimate: FareCalculation
        ): Ride {
            return Ride(
                pickupLocation = pickupLocation,
                destinationLocation = destinationLocation,
                fareEstimate = fareEstimate,
                status = RideStatus.REQUESTED,
                requestTime = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime.now()
                } else {
                    // Fallback for older Android versions
                    LocalDateTime.of(2024, 1, 1, 0, 0)
                }
            )
        }
    }
}

enum class RideStatus {
    REQUESTED,
    CONFIRMED,
    DRIVER_ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    fun getDisplayName(): String {
        return when (this) {
            REQUESTED -> "Requested"
            CONFIRMED -> "Confirmed"
            DRIVER_ASSIGNED -> "Driver Assigned"
            IN_PROGRESS -> "In Progress"
            COMPLETED -> "Completed"
            CANCELLED -> "Cancelled"
        }
    }

    fun getColor(): String {
        return when (this) {
            REQUESTED -> "#FF9800" // Orange
            CONFIRMED -> "#2196F3" // Blue
            DRIVER_ASSIGNED -> "#9C27B0" // Purple
            IN_PROGRESS -> "#FF5722" // Deep Orange
            COMPLETED -> "#4CAF50" // Green
            CANCELLED -> "#F44336" // Red
        }
    }
}