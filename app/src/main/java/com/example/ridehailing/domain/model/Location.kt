package com.example.ridehailing.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Location data class representing a geographic location
 */

@Parcelize
data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String = ""
) : Parcelable {

    companion object {
        val EMPTY = Location(0.0, 0.0, "")

    }

    fun isEmpty(): Boolean = this == EMPTY

    fun isValid(): Boolean {
        return latitude != 0.0 && longitude != 0.0 &&
                latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

}