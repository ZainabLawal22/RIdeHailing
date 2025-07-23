package com.example.ridehailing.domain.repository

import com.example.ridehailing.domain.model.FareCalculation
import com.example.ridehailing.domain.model.Location
import com.example.ridehailing.domain.model.Ride
import com.example.ridehailing.domain.model.RideStatus
import kotlinx.coroutines.flow.Flow



interface RideRepository {

    // Local database operations
    fun getAllRides(): Flow<List<Ride>>
    suspend fun getRideById(rideId: Long): Ride?
    suspend fun insertRide(ride: Ride): Long
    suspend fun updateRide(ride: Ride)
    suspend fun deleteRide(ride: Ride)
    fun getRidesByStatus(status: RideStatus): Flow<List<Ride>>

    // API operations => mocked
    suspend fun getFareEstimate(pickup: Location, destination: Location): Result<FareCalculation>
    suspend fun requestRide(pickup: Location, destination: Location, fareEstimate: FareCalculation): Result<Ride>
}