package com.example.ridehailing.di


import com.example.ridehailing.data.repository.RideRepositoryImpl
import com.example.ridehailing.domain.repository.RideRepository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRideRepository(
        rideRepositoryImpl: RideRepositoryImpl
    ): RideRepository
}