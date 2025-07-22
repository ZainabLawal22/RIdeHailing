package com.example.ridehailing.presentation.ui.history

import com.example.ridehailing.domain.usecase.GetRideHistoryUseCase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.ridehailing.domain.model.Ride
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RideHistoryViewModel @Inject constructor(
    private val getRideHistoryUseCase: GetRideHistoryUseCase
) : ViewModel() {

    // Ride History
    val rideHistory: LiveData<List<Ride>> = getRideHistoryUseCase().asLiveData()

    // UI State
    private val _uiState = MutableLiveData<RideHistoryUiState>()
    val uiState: LiveData<RideHistoryUiState> = _uiState

    init {
        _uiState.value = RideHistoryUiState.LOADING

        // Observe ride history to update UI state
        rideHistory.observeForever { rides ->
            _uiState.value = if (rides.isEmpty()) {
                RideHistoryUiState.EMPTY
            } else {
                RideHistoryUiState.LOADED
            }
        }
    }

    fun refreshRideHistory() {
        _uiState.value = RideHistoryUiState.LOADING
        // The Flow will automatically update the UI
    }
}

enum class RideHistoryUiState {
    LOADING,
    LOADED,
    EMPTY,
    ERROR
}