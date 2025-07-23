package com.example.ridehailing.domain.usecase



import com.example.ridehailing.domain.model.FareCalculation
import com.example.ridehailing.domain.model.Location
import com.example.ridehailing.domain.repository.RideRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject


class CalculateFareUseCase @Inject constructor(
    private val repository: RideRepository
) {

    suspend operator fun invoke(pickup: Location, destination: Location): Result<FareCalculation> {
        // Validate input locations
        if (pickup.isEmpty() || destination.isEmpty()) {
            return Result.failure(IllegalArgumentException("Pickup and destination locations are required"))
        }

        if (!pickup.isValid() || !destination.isValid()) {
            return Result.failure(IllegalArgumentException("Invalid location coordinates"))
        }

        return repository.getFareEstimate(pickup, destination)
    }
}