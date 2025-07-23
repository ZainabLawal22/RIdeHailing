package com.example.ridehailing.data.api.models

/**
 * API response models for mocked backend
 */
data class FareEstimateRequest(
    val pickup: String,
    val destination: String
)