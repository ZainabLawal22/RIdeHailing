package com.example.ridehailing.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Location data class representing a geographic location
 */
//@Parcelize
//data class Location(
//    val latitude: Double,
//    val longitude: Double,
//    val address: String = ""
//) : Parcelable {
//
//    companion object {
//        val EMPTY = Location(0.0, 0.0, "")
//    }
//
//    fun isEmpty(): Boolean = this == EMPTY
//
//    fun isValid(): Boolean = latitude != 0.0 && longitude != 0.0
//}



@Parcelize
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String = ""
) : Parcelable {

    companion object {
        val EMPTY = Location(0.0, 0.0, "")

        // Nigerian coordinate boundaries for validation
        const val NIGERIA_MIN_LAT = 4.0
        const val NIGERIA_MAX_LAT = 14.0
        const val NIGERIA_MIN_LNG = 2.5
        const val NIGERIA_MAX_LNG = 15.0
    }

    fun isEmpty(): Boolean = this == EMPTY

    fun isValid(): Boolean {
        return latitude != 0.0 && longitude != 0.0 &&
                latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

    fun isInNigeria(): Boolean {
        return latitude in NIGERIA_MIN_LAT..NIGERIA_MAX_LAT &&
                longitude in NIGERIA_MIN_LNG..NIGERIA_MAX_LNG
    }

    fun getDisplayAddress(): String {
        return if (address.isNotEmpty()) {
            address
        } else {
            "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}"
        }
    }
}