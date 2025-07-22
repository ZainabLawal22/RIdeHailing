package com.example.ridehailing.presentation.ui.home

import com.example.ridehailing.domain.usecase.CalculateFareUseCase
import com.example.ridehailing.domain.usecase.RequestRideUseCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridehailing.domain.model.FareCalculation
import com.example.ridehailing.domain.model.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val getFareEstimateUseCase: CalculateFareUseCase,
//    private val requestRideUseCase: RequestRideUseCase
//) : ViewModel() {
//
//    // UI State
//    private val _uiState = MutableLiveData<HomeUiState>()
//    val uiState: LiveData<HomeUiState> = _uiState
//
//    // Locations
//    private val _pickupLocation = MutableLiveData<Location>()
//    val pickupLocation: LiveData<Location> = _pickupLocation
//
//    private val _destinationLocation = MutableLiveData<Location>()
//    val destinationLocation: LiveData<Location> = _destinationLocation
//
//    // Fare Estimate
//    private val _fareEstimate = MutableLiveData<FareCalculation?>()
//    val fareEstimate: LiveData<FareCalculation?> = _fareEstimate
//
//    // Error Messages
//    private val _errorMessage = MutableLiveData<String?>()
//    val errorMessage: LiveData<String?> = _errorMessage
//
//    // Success Messages
//    private val _successMessage = MutableLiveData<String?>()
//    val successMessage: LiveData<String?> = _successMessage
//
//    init {
//        _uiState.value = HomeUiState.INITIAL
//    }
//
//    fun setPickupLocation(location: Location) {
//        _pickupLocation.value = location
//        clearFareEstimate()
//    }
//
//    fun setDestinationLocation(location: Location) {
//        _destinationLocation.value = location
//        clearFareEstimate()
//    }
//
//    fun getFareEstimate() {
//        val pickup = _pickupLocation.value
//        val destination = _destinationLocation.value
//
//        if (pickup == null || destination == null) {
//            _errorMessage.value = "Please select both pickup and destination locations"
//            return
//        }
//
//        if (!pickup.isValid() || !destination.isValid()) {
//            _errorMessage.value = "Invalid location coordinates"
//            return
//        }
//
//        _uiState.value = HomeUiState.LOADING_FARE
//
//        viewModelScope.launch {
//            getFareEstimateUseCase(pickup, destination)
//                .onSuccess { fareEstimate ->
//                    _fareEstimate.value = fareEstimate
//                    _uiState.value = HomeUiState.FARE_LOADED
//                }
//                .onFailure { error ->
//                    _errorMessage.value = error.message ?: "Failed to get fare estimate"
//                    _uiState.value = HomeUiState.ERROR
//                }
//        }
//    }
//
//    fun requestRide() {
//        val pickup = _pickupLocation.value
//        val destination = _destinationLocation.value
//        val fare = _fareEstimate.value
//
//        if (pickup == null || destination == null || fare == null) {
//            _errorMessage.value = "Please get fare estimate first"
//            return
//        }
//
//        _uiState.value = HomeUiState.REQUESTING_RIDE
//
//        viewModelScope.launch {
//            requestRideUseCase(pickup, destination, fare)
//                .onSuccess { ride ->
//                    _successMessage.value = "Ride requested successfully! Driver: ${ride.driver?.name}"
//                    _uiState.value = HomeUiState.RIDE_REQUESTED
//                    clearData()
//                }
//                .onFailure { error ->
//                    _errorMessage.value = error.message ?: "Failed to request ride"
//                    _uiState.value = HomeUiState.ERROR
//                }
//        }
//    }
//
//    fun clearErrorMessage() {
//        _errorMessage.value = null
//    }
//
//    fun clearSuccessMessage() {
//        _successMessage.value = null
//    }
//
//    private fun clearFareEstimate() {
//        _fareEstimate.value = null
//        if (_uiState.value == HomeUiState.FARE_LOADED) {
//            _uiState.value = HomeUiState.LOCATIONS_SET
//        }
//    }
//
//    private fun clearData() {
//        _pickupLocation.value = Location.EMPTY
//        _destinationLocation.value = Location.EMPTY
//        _fareEstimate.value = null
//    }
//
//    fun canRequestFare(): Boolean {
//        val pickup = _pickupLocation.value
//        val destination = _destinationLocation.value
//        return pickup != null && destination != null &&
//                pickup.isValid() && destination.isValid() &&
//                _uiState.value != HomeUiState.LOADING_FARE
//    }
//
//    fun canRequestRide(): Boolean {
//        return _fareEstimate.value != null &&
//                _uiState.value != HomeUiState.REQUESTING_RIDE
//    }
//}
//
//enum class HomeUiState {
//    INITIAL,
//    LOCATIONS_SET,
//    LOADING_FARE,
//    FARE_LOADED,
//    REQUESTING_RIDE,
//    RIDE_REQUESTED,
//    ERROR
//}


//class HomeViewModel : ViewModel() {
//
//    // UI State
//    private val _uiState = MutableLiveData<HomeUiState>()
//    val uiState: LiveData<HomeUiState> = _uiState
//
//    // Locations
//    private val _pickupLocation = MutableLiveData<Location>()
//    val pickupLocation: LiveData<Location> = _pickupLocation
//
//    private val _destinationLocation = MutableLiveData<Location>()
//    val destinationLocation: LiveData<Location> = _destinationLocation
//
//    // Fare Estimate
//    private val _fareEstimate = MutableLiveData<FareCalculation?>()
//    val fareEstimate: LiveData<FareCalculation?> = _fareEstimate
//
//    // Error Messages
//    private val _errorMessage = MutableLiveData<String?>()
//    val errorMessage: LiveData<String?> = _errorMessage
//
//    // Success Messages
//    private val _successMessage = MutableLiveData<String?>()
//    val successMessage: LiveData<String?> = _successMessage
//
//    init {
//        _uiState.value = HomeUiState.INITIAL
//    }
//
//    fun setPickupLocation(location: Location) {
//        _pickupLocation.value = location
//        clearFareEstimate()
//    }
//
//    fun setDestinationLocation(location: Location) {
//        _destinationLocation.value = location
//        clearFareEstimate()
//    }
//
//    fun getFareEstimate() {
//        val pickup = _pickupLocation.value
//        val destination = _destinationLocation.value
//
//        if (pickup == null || destination == null) {
//            _errorMessage.value = "Please select both pickup and destination locations"
//            return
//        }
//
//        if (!pickup.isValid() || !destination.isValid()) {
//            _errorMessage.value = "Invalid location coordinates"
//            return
//        }
//
//        _uiState.value = HomeUiState.LOADING_FARE
//
//        // Simulate fare calculation without dependencies
//        viewModelScope.launch {
//            delay(1000) // Simulate network delay
//
//            // Mock fare calculation
//            val distance = 5.0 // Mock distance
//            val baseFare = 2.5
//            val distanceFare = distance * 1.0
//            val demandMultiplier = 1.5
//            val trafficMultiplier = 1.0
//            val totalFare = (baseFare + distanceFare) * demandMultiplier * trafficMultiplier
//
//            val mockFare = FareCalculation(
//                baseFare = baseFare,
//                distanceFare = distanceFare,
//                demandMultiplier = demandMultiplier,
//                trafficMultiplier = trafficMultiplier,
//                totalFare = totalFare,
//                distance = distance,
//                estimatedDuration = 12
//            )
//
//            _fareEstimate.value = mockFare
//            _uiState.value = HomeUiState.FARE_LOADED
//        }
//    }
//
//    fun requestRide() {
//        val pickup = _pickupLocation.value
//        val destination = _destinationLocation.value
//        val fare = _fareEstimate.value
//
//        if (pickup == null || destination == null || fare == null) {
//            _errorMessage.value = "Please get fare estimate first"
//            return
//        }
//
//        _uiState.value = HomeUiState.REQUESTING_RIDE
//
//        // Simulate ride request without dependencies
//        viewModelScope.launch {
//            delay(1500) // Simulate network delay
//
//            _successMessage.value = "Ride requested successfully! Driver: John Doe (Toyota Prius XYZ-1234) - ETA: 5 min"
//            _uiState.value = HomeUiState.RIDE_REQUESTED
//            clearData()
//        }
//    }
//
//    fun clearErrorMessage() {
//        _errorMessage.value = null
//    }
//
//    fun clearSuccessMessage() {
//        _successMessage.value = null
//    }
//
//    private fun clearFareEstimate() {
//        _fareEstimate.value = null
//        if (_uiState.value == HomeUiState.FARE_LOADED) {
//            _uiState.value = HomeUiState.LOCATIONS_SET
//        }
//    }
//
//    private fun clearData() {
//        _pickupLocation.value = Location.EMPTY
//        _destinationLocation.value = Location.EMPTY
//        _fareEstimate.value = null
//    }
//
//    fun canRequestFare(): Boolean {
//        val pickup = _pickupLocation.value
//        val destination = _destinationLocation.value
//        return pickup != null && destination != null &&
//                pickup.isValid() && destination.isValid() &&
//                _uiState.value != HomeUiState.LOADING_FARE
//    }
//
//    fun canRequestRide(): Boolean {
//        return _fareEstimate.value != null &&
//                _uiState.value != HomeUiState.REQUESTING_RIDE
//    }
//}
//
//enum class HomeUiState {
//    INITIAL,
//    LOCATIONS_SET,
//    LOADING_FARE,
//    FARE_LOADED,
//    REQUESTING_RIDE,
//    RIDE_REQUESTED,
//    ERROR
//}



class HomeViewModel : ViewModel() {

    // UI State
    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    // Locations
    private val _pickupLocation = MutableLiveData<Location>()
    val pickupLocation: LiveData<Location> = _pickupLocation

    private val _destinationLocation = MutableLiveData<Location>()
    val destinationLocation: LiveData<Location> = _destinationLocation

    // Fare Estimate
    private val _fareEstimate = MutableLiveData<FareCalculation?>()
    val fareEstimate: LiveData<FareCalculation?> = _fareEstimate

    // Error Messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Success Messages
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

        // Simulate fare calculation with realistic Nigerian distances
        viewModelScope.launch {
            delay(1000) // Simulate network delay

            try {
                // Calculate distance between pickup and destination (simplified)
                val distance = calculateDistance(pickup, destination)
                val baseFare = 500.0 // Nigerian Naira base fare
                val distanceFare = distance * 100.0 // ₦100 per km
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
                    estimatedDuration = (distance * 3).toInt() // 3 minutes per km in traffic
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

        // Simulate ride request
        viewModelScope.launch {
            delay(1500) // Simulate network delay

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

                // Reset after successful request
                delay(3000)
                clearData()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to request ride: ${e.message}"
                _uiState.value = HomeUiState.ERROR
            }
        }
    }

    private fun calculateDistance(pickup: Location, destination: Location): Double {
        // Simple distance calculation using Haversine formula
        val earthRadius = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(destination.latitude - pickup.latitude)
        val dLon = Math.toRadians(destination.longitude - pickup.longitude)

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(pickup.latitude)) * kotlin.math.cos(Math.toRadians(destination.latitude)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)

        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

        return earthRadius * c
    }

    private fun getDemandMultiplier(): Double {
        // Nigerian peak hours: 7-9 AM, 5-8 PM
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            currentHour in 7..9 || currentHour in 17..20 -> 1.5 // Peak hours
            currentHour in 22..23 || currentHour in 0..5 -> 1.2 // Late night
            else -> 1.0 // Normal hours
        }
    }

    private fun getTrafficMultiplier(): Double {
        // Simulate Lagos traffic conditions
        return when ((1..4).random()) {
            1 -> 1.4 // Heavy Lagos traffic
            2 -> 1.2 // Moderate traffic
            3 -> 1.1 // Light traffic
            else -> 1.0 // Free flow
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