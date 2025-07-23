package com.example.ridehailing.data.repository


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ridehailing.data.api.RideApiService
import com.example.ridehailing.data.database.dao.RideDao
import com.example.ridehailing.domain.model.FareCalculation
import com.example.ridehailing.domain.model.Location
import com.example.ridehailing.domain.model.Ride
import com.example.ridehailing.domain.model.RideStatus
import com.example.ridehailing.domain.repository.RideRepository
import com.example.ridehailing.utils.LocationUtils
import com.example.ridehailing.utils.toDomain
import com.example.ridehailing.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RideRepositoryImpl @Inject constructor(
    private val rideDao: RideDao,
    private val apiService: RideApiService
) : RideRepository {

    override fun getAllRides(): Flow<List<Ride>> {
        return rideDao.getAllRides().map { entities ->
            entities.toDomain()
        }
    }

    override suspend fun getRideById(rideId: Long): Ride? {
        return rideDao.getRideById(rideId)?.toDomain()
    }

    override suspend fun insertRide(ride: Ride): Long {
        return rideDao.insertRide(ride.toEntity())
    }

    override suspend fun updateRide(ride: Ride) {
        rideDao.updateRide(ride.toEntity())
    }

    override suspend fun deleteRide(ride: Ride) {
        rideDao.deleteRide(ride.toEntity())
    }

    override fun getRidesByStatus(status: RideStatus): Flow<List<Ride>> {
        return rideDao.getRidesByStatus(status).map { entities ->
            entities.toDomain()
        }
    }

    override suspend fun getFareEstimate(pickup: Location, destination: Location): Result<FareCalculation> {
        return try {
            val pickupString = LocationUtils.formatCoordinates(pickup.latitude, pickup.longitude)
            val destinationString = LocationUtils.formatCoordinates(destination.latitude, destination.longitude)

            val response = apiService.getFareEstimate(pickupString, destinationString)

            if (response.isSuccessful && response.body() != null) {
                val fareEstimateResponse = response.body()!!
                val fareEstimate = fareEstimateResponse.toDomain()
                Result.success(fareEstimate)
            } else {
                Result.failure(Exception("Failed to get fare estimate: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun requestRide(pickup: Location, destination: Location, fareCalculation: FareCalculation): Result<Ride> {
        return try {
            val pickupString = LocationUtils.formatCoordinates(pickup.latitude, pickup.longitude)
            val destinationString = LocationUtils.formatCoordinates(destination.latitude, destination.longitude)

            val response = apiService.requestRide(pickupString, destinationString, fareCalculation.totalFare)

            if (response.isSuccessful && response.body() != null) {
                val rideResponse = response.body()!!

                val driver = rideResponse.driver.toDomain().copy(
                    estimatedArrival = rideResponse.estimatedArrival
                )

                val ride = Ride(
                    pickupLocation = pickup,
                    destinationLocation = destination,
                    fareEstimate = fareCalculation,
                    driver = driver,
                    status = when (rideResponse.status) {
                        "confirmed" -> RideStatus.CONFIRMED
                        else -> RideStatus.REQUESTED
                    },
                    requestTime = LocalDateTime.now()
                )

                // Save to local database
                val rideId = insertRide(ride)
                val savedRide = ride.copy(id = rideId)

                Result.success(savedRide)
            } else {
                Result.failure(Exception("Failed to request ride: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}