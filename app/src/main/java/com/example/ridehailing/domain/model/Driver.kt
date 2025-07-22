package com.example.ridehailing.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Driver information data class
 */
//data class Driver(
//    val name: String,
//    val car: String,
//    val plateNumber: String,
//    val rating: Float = 0.0f,
//    val estimatedArrival: String = ""
//)


data class Driver(
    val name: String,
    val car: String,
    val plateNumber: String,
    val rating: Float = 0.0f,
    val estimatedArrival: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = ""
) {

    fun getDisplayName(): String {
        return name.ifEmpty { "Unknown Driver" }
    }

    fun getVehicleInfo(): String {
        return if (car.isNotEmpty() && plateNumber.isNotEmpty()) {
            "$car - $plateNumber"
        } else if (car.isNotEmpty()) {
            car
        } else {
            "Vehicle info not available"
        }
    }

    fun getFormattedRating(): String {
        return if (rating > 0) {
            "${String.format("%.1f", rating)} ⭐"
        } else {
            "No rating"
        }
    }

    fun getEstimatedArrivalDisplay(): String {
        return if (estimatedArrival.isNotEmpty()) {
            "ETA: $estimatedArrival"
        } else {
            "ETA: Unknown"
        }
    }

    fun isValidDriver(): Boolean {
        return name.isNotEmpty() && car.isNotEmpty() && plateNumber.isNotEmpty()
    }

    companion object {
        val EMPTY = Driver("", "", "", 0.0f, "", "", "")
    }
}