package com.example.ridehailing.data.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ridehailing.data.api.models.DriverResponse
import com.example.ridehailing.data.api.models.FareEstimateResponse
import com.example.ridehailing.data.api.models.RideRequestResponse
import com.example.ridehailing.domain.model.FareCalculation
import com.example.ridehailing.utils.LocationUtils
import kotlinx.coroutines.delay
import retrofit2.Response
import java.time.LocalTime
import kotlin.random.Random


class MockRideApiService : RideApiService {

    private val mockDrivers = listOf(
        DriverResponse("John Doe", "Toyota Prius", "XYZ-1234", 4.8f),
        DriverResponse("Sarah Smith", "Honda Civic", "ABC-5678", 4.7f),
        DriverResponse("Mike Johnson", "Nissan Altima", "DEF-9012", 4.6f),
        DriverResponse("Lisa Brown", "Hyundai Elantra", "GHI-3456", 4.9f),
        DriverResponse("David Wilson", "Toyota Camry", "JKL-7890", 4.5f)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getFareEstimate(pickup: String, destination: String): Response<FareEstimateResponse> {
        // Simulate network delay
        delay(1000L + Random.nextLong(500L))

        try {
            val pickupCoords = pickup.split(",")
            val destCoords = destination.split(",")

            val pickupLat = pickupCoords[0].toDouble()
            val pickupLng = pickupCoords[1].toDouble()
            val destLat = destCoords[0].toDouble()
            val destLng = destCoords[1].toDouble()

            // Calculate distance using Haversine formula
            val distance = LocationUtils.calculateDistance(pickupLat, pickupLng, destLat, destLng)

            // Calculate fare components
            val baseFare = FareCalculation.BASE_FARE
            val distanceFare = distance * FareCalculation.PER_KM_RATE
            val demandMultiplier = getDemandMultiplier()
            val trafficMultiplier = getTrafficMultiplier()

            val totalFare = (baseFare + distanceFare) * demandMultiplier * trafficMultiplier
            val estimatedDuration = (distance * 2.5).toInt() // Rough estimate: 2.5 min per km

            val response = FareEstimateResponse(
                baseFare = baseFare,
                distanceFare = distanceFare,
                demandMultiplier = demandMultiplier,
                trafficMultiplier = trafficMultiplier,
                totalFare = totalFare,
                distance = distance,
                estimatedDuration = estimatedDuration
            )

            return Response.success(response)
        } catch (e: Exception) {
            return Response.error(400, okhttp3.ResponseBody.create(null, "Invalid coordinates"))
        }
    }

    override suspend fun requestRide(pickup: String, destination: String, fare: Double): Response<RideRequestResponse> {
        // Simulate network delay
        delay(1500L + Random.nextLong(1000L))

        val randomDriver = mockDrivers.random()
        val estimatedArrival = "${Random.nextInt(3, 8)} min"

        val response = RideRequestResponse(
            status = "confirmed",
            driver = randomDriver,
            estimatedArrival = estimatedArrival
        )

        return Response.success(response)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDemandMultiplier(): Double {
        val currentHour = LocalTime.now().hour
        return when {
            currentHour in 7..9 || currentHour in 17..19 -> FareCalculation.PEAK_HOUR_MULTIPLIER // Peak hours
            currentHour in 22..23 || currentHour in 0..5 -> 1.2 // Late night
            else -> FareCalculation.NORMAL_MULTIPLIER // Normal hours
        }
    }

    private fun getTrafficMultiplier(): Double {
        // Simulate random traffic conditions
        return when (Random.nextInt(1, 4)) {
            1 -> FareCalculation.HEAVY_TRAFFIC_MULTIPLIER // Heavy traffic
            2 -> 1.1 // Light traffic
            else -> FareCalculation.NORMAL_MULTIPLIER // Normal traffic
        }
    }
}