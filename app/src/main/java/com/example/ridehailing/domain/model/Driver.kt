package com.example.ridehailing.domain.model

/**
 * Driver information data class
 */

data class Driver(
    val name: String,
    val car: String,
    val plateNumber: String,
    val rating: Float = 0.0f,
    val estimatedArrival: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = ""
)