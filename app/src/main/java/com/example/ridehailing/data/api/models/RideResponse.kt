package com.example.ridehailing.data.api.models

import com.google.gson.annotations.SerializedName

data class FareEstimateResponse(
    @SerializedName("base_fare")
    val baseFare: Double,
    @SerializedName("distance_fare")
    val distanceFare: Double,
    @SerializedName("demand_multiplier")
    val demandMultiplier: Double,
    @SerializedName("traffic_multiplier")
    val trafficMultiplier: Double = 1.0,
    @SerializedName("total_fare")
    val totalFare: Double,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("estimated_duration")
    val estimatedDuration: Int
)

data class RideRequestResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("driver")
    val driver: DriverResponse,
    @SerializedName("estimated_arrival")
    val estimatedArrival: String
)

data class DriverResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("car")
    val car: String,
    @SerializedName("plate_number")
    val plateNumber: String,
    @SerializedName("rating")
    val rating: Float = 4.5f
)