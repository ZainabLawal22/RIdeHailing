package com.example.ridehailing.presentation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridehailing.domain.model.FareCalculation
import com.example.ridehailing.domain.model.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {

    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    private val _pickupLocation = MutableLiveData<Location>()
    val pickupLocation: LiveData<Location> = _pickupLocation

    private val _destinationLocation = MutableLiveData<Location>()
    val destinationLocation: LiveData<Location> = _destinationLocation


    private val _fareEstimate = MutableLiveData<FareCalculation?>()
    val fareEstimate: LiveData<FareCalculation?> = _fareEstimate


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    init {
        _uiState.value = HomeUiState.INITIAL
    }

    fun setPickupLocation(location: Location) {
        _pickupLocation.value = location
        clearFareEstimate()
        updateUIState()
    }

    fun setDestinationLocation(location: Location) {
        _destinationLocation.value = location
        clearFareEstimate()
        updateUIState()
    }

    private fun updateUIState() {
        val pickup = _pickupLocation.value
        val destination = _destinationLocation.value

        when {
            pickup != null && destination != null && pickup.isValid() && destination.isValid() -> {
                _uiState.value = HomeUiState.LOCATIONS_SET
            }
            pickup != null && pickup.isValid() -> {
                _uiState.value = HomeUiState.PICKUP_SET
            }
            _uiState.value != HomeUiState.LOADING_FARE && _uiState.value != HomeUiState.REQUESTING_RIDE -> {
                _uiState.value = HomeUiState.INITIAL
            }
        }
    }

    fun getFareEstimate() {
        val pickup = _pickupLocation.value
        val destination = _destinationLocation.value

        if (pickup == null || destination == null) {
            _errorMessage.value = "Please select both pickup and destination locations"
            return
        }

        if (!pickup.isValid() || !destination.isValid()) {
            _errorMessage.value = "Invalid location coordinates"
            return
        }

        _uiState.value = HomeUiState.LOADING_FARE


        viewModelScope.launch {
            delay(1000)

            try {
                // Calculate distance between pickup and destination
                val distance = calculateDistance(pickup, destination)
                val baseFare = 500.0
                val distanceFare = distance * 100.0
                val demandMultiplier = getDemandMultiplier()
                val trafficMultiplier = getTrafficMultiplier()
                val totalFare = (baseFare + distanceFare) * demandMultiplier * trafficMultiplier

                val mockFare = FareCalculation(
                    baseFare = baseFare,
                    distanceFare = distanceFare,
                    demandMultiplier = demandMultiplier,
                    trafficMultiplier = trafficMultiplier,
                    totalFare = totalFare,
                    distance = distance,
                    estimatedDuration = (distance * 3).toInt()
                )

                _fareEstimate.value = mockFare
                _uiState.value = HomeUiState.FARE_LOADED
            } catch (e: Exception) {
                _errorMessage.value = "Failed to calculate fare: ${e.message}"
                _uiState.value = HomeUiState.ERROR
            }
        }
    }

    fun requestRide() {
        val pickup = _pickupLocation.value
        val destination = _destinationLocation.value
        val fare = _fareEstimate.value

        if (pickup == null || destination == null || fare == null) {
            _errorMessage.value = "Please get fare estimate first"
            return
        }

        _uiState.value = HomeUiState.REQUESTING_RIDE


        viewModelScope.launch {
            delay(1500)

            try {
                val driverNames = listOf("Adebayo Johnson", "Chioma Okafor", "Ibrahim Musa", "Funmi Adebayo", "Emeka Nwoko")
                val carModels = listOf("Toyota Corolla", "Honda Accord", "Hyundai Elantra", "Kia Rio", "Nissan Sentra")
                val plateNumbers = listOf("ABC-123-DE", "KJA-456-FG", "LSG-789-HI", "MUR-012-JK", "NGR-345-LM")

                val randomIndex = (0..4).random()
                val driverName = driverNames[randomIndex]
                val carModel = carModels[randomIndex]
                val plateNumber = plateNumbers[randomIndex]
                val eta = (3..8).random()

                _successMessage.value = "Ride confirmed! Driver: $driverName ($carModel - $plateNumber) - ETA: $eta min"
                _uiState.value = HomeUiState.RIDE_REQUESTED


                delay(3000)
                clearData()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to request ride: ${e.message}"
                _uiState.value = HomeUiState.ERROR
            }
        }
    }

    private fun calculateDistance(pickup: Location, destination: Location): Double {
        val earthRadius = 6371.0

        val dLat = Math.toRadians(destination.latitude - pickup.latitude)
        val dLon = Math.toRadians(destination.longitude - pickup.longitude)

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(pickup.latitude)) * kotlin.math.cos(Math.toRadians(destination.latitude)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)

        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return earthRadius * c
    }

    private fun getDemandMultiplier(): Double {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            currentHour in 7..9 || currentHour in 17..20 -> 1.5
            currentHour in 22..23 || currentHour in 0..5 -> 1.2
            else -> 1.0
        }
    }

    private fun getTrafficMultiplier(): Double {

        return when ((1..4).random()) {
            1 -> 1.4
            2 -> 1.2
            3 -> 1.1
            else -> 1.0
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    private fun clearFareEstimate() {
        _fareEstimate.value = null
        if (_uiState.value == HomeUiState.FARE_LOADED) {
            updateUIState()
        }
    }

    private fun clearData() {
        _pickupLocation.value = Location.EMPTY
        _destinationLocation.value = Location.EMPTY
        _fareEstimate.value = null
        _uiState.value = HomeUiState.INITIAL
    }

    fun canRequestFare(): Boolean {
        val pickup = _pickupLocation.value
        val destination = _destinationLocation.value
        return pickup != null && destination != null &&
                pickup.isValid() && destination.isValid() &&
                _uiState.value != HomeUiState.LOADING_FARE
    }

    fun canRequestRide(): Boolean {
        return _fareEstimate.value != null &&
                _uiState.value != HomeUiState.REQUESTING_RIDE
    }
}

enum class HomeUiState {
    INITIAL,
    PICKUP_SET,
    LOCATIONS_SET,
    LOADING_FARE,
    FARE_LOADED,
    REQUESTING_RIDE,
    RIDE_REQUESTED,
    ERROR
}