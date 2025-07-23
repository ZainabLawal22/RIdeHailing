package com.example.ridehailing.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ridehailing.domain.model.RideStatus
import java.time.LocalDateTime


/**
 * Room entity for storing ride history
 */
@Entity(tableName = "rides")
data class EntityRide(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val pickupLatitude: Double,
    val pickupLongitude: Double,
    val pickupAddress: String,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val destinationAddress: String,
    val baseFare: Double,
    val distanceFare: Double,
    val demandMultiplier: Double,
    val trafficMultiplier: Double,
    val totalFare: Double,
    val distance: Double,
    val estimatedDuration: Int,
    val driverName: String? = null,
    val driverCar: String? = null,
    val driverPlateNumber: String? = null,
    val driverRating: Float? = null,
    val estimatedArrival: String? = null,
    val status: RideStatus,
    val requestTime: LocalDateTime,
    val completionTime: LocalDateTime? = null
)