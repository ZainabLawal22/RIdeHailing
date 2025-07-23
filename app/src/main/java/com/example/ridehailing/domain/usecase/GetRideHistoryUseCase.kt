package com.example.ridehailing.domain.usecase

import com.example.ridehailing.domain.model.Ride
import com.example.ridehailing.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetRideHistoryUseCase @Inject constructor(
    private val repository: RideRepository
) {

    operator fun invoke(): Flow<List<Ride>> {
        return repository.getAllRides()
    }
}