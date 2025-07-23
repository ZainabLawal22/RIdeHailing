package com.example.ridehailing.presentation.ui.history

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.ridehailing.domain.usecase.GetRideHistoryUseCase


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridehailing.domain.model.Ride
import com.example.ridehailing.domain.model.RideStatus
import com.example.ridehailing.domain.model.Location
import com.example.ridehailing.domain.model.FareCalculation
import com.example.ridehailing.domain.model.Driver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class RideHistoryViewModel : ViewModel() {

    private val _rideHistory = MutableLiveData<List<Ride>>()
    val rideHistory: LiveData<List<Ride>> = _rideHistory

    private val _uiState = MutableLiveData<RideHistoryUiState>()
    val uiState: LiveData<RideHistoryUiState> = _uiState

    init {
        _uiState.value = RideHistoryUiState.LOADING
        loadMockRideHistory()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMockRideHistory() {
        viewModelScope.launch {
            delay(1000)


            val mockRides = createMockRides()

            _rideHistory.value = mockRides
            _uiState.value = if (mockRides.isEmpty()) {
                RideHistoryUiState.EMPTY
            } else {
                RideHistoryUiState.LOADED
            }
        }
    }

    fun refreshRideHistory() {
        _uiState.value = RideHistoryUiState.LOADING
        loadMockRideHistory()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMockRides(): List<Ride> {

        val mockRides = mutableListOf<Ride>()

        try {

            val ride1 = Ride(
                id = 1,
                pickupLocation = Location(6.5244, 3.3792, "Victoria Island, Lagos"),
                destinationLocation = Location(6.4281, 3.4219, "Ikeja, Lagos"),
                fareEstimate = FareCalculation(
                    baseFare = 2.5,
                    distanceFare = 8.0,
                    demandMultiplier = 1.5,
                    trafficMultiplier = 1.3,
                    totalFare = 20.45,
                    distance = 8.0,
                    estimatedDuration = 25
                ),
                driver = Driver(
                    name = "John Doe",
                    car = "Toyota Corolla",
                    plateNumber = "LAG-123-AB",
                    rating = 4.8f,
                    estimatedArrival = "5 min"
                ),
                status = RideStatus.COMPLETED,
                requestTime = getCurrentTime().minusHours(2),
                completionTime = getCurrentTime().minusHours(1).minusMinutes(35)
            )


            val ride2 = Ride(
                id = 2,
                pickupLocation = Location(6.4281, 3.4219, "Ikeja, Lagos"),
                destinationLocation = Location(6.5952, 3.3621, "Lekki Phase 1, Lagos"),
                fareEstimate = FareCalculation(
                    baseFare = 2.5,
                    distanceFare = 12.0,
                    demandMultiplier = 1.0,
                    trafficMultiplier = 1.1,
                    totalFare = 15.95,
                    distance = 12.0,
                    estimatedDuration = 30
                ),
                driver = Driver(
                    name = "Sarah Smith",
                    car = "Honda Civic",
                    plateNumber = "LAG-456-CD",
                    rating = 4.9f,
                    estimatedArrival = "3 min"
                ),
                status = RideStatus.COMPLETED,
                requestTime = getCurrentTime().minusDays(1),
                completionTime = getCurrentTime().minusDays(1).plusMinutes(30)
            )


            val ride3 = Ride(
                id = 3,
                pickupLocation = Location(6.5355, 3.3487, "Surulere, Lagos"),
                destinationLocation = Location(6.4698, 3.5852, "Festac Town, Lagos"),
                fareEstimate = FareCalculation(
                    baseFare = 2.5,
                    distanceFare = 6.0,
                    demandMultiplier = 1.2,
                    trafficMultiplier = 1.0,
                    totalFare = 10.20,
                    distance = 6.0,
                    estimatedDuration = 18
                ),
                driver = Driver(
                    name = "Mike Johnson",
                    car = "Hyundai Elantra",
                    plateNumber = "LAG-789-EF",
                    rating = 4.7f,
                    estimatedArrival = "2 min"
                ),
                status = RideStatus.IN_PROGRESS,
                requestTime = getCurrentTime().minusMinutes(15)
            )

            mockRides.add(ride1)
            mockRides.add(ride2)
            mockRides.add(ride3)

        } catch (e: Exception) {

            return emptyList()
        }

        return mockRides.sortedByDescending { it.requestTime }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime(): LocalDateTime {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime.now()
            } else {
                LocalDateTime.of(2024, 7, 23, 12, 0)
            }
        } catch (e: Exception) {
            LocalDateTime.of(2024, 7, 23, 12, 0)
        }
    }
}

enum class RideHistoryUiState {
    LOADING,
    LOADED,
    EMPTY,
    ERROR
}