package com.example.ridehailing.data.api

import com.example.ridehailing.data.api.models.FareEstimateResponse
import com.example.ridehailing.data.api.models.RideRequestResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RideApiService {

    @GET("/api/fare-estimate")
    suspend fun getFareEstimate(
        @Query("pickup") pickup: String,
        @Query("destination") destination: String
    ): Response<FareEstimateResponse>

    @POST("/api/request-ride")
    suspend fun requestRide(
        @Query("pickup") pickup: String,
        @Query("destination") destination: String,
        @Query("fare") fare: Double
    ): Response<RideRequestResponse>
}