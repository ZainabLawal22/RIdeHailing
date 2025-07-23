package com.example.ridehailing.di

import android.content.Context
import androidx.room.Room
import com.example.ridehailing.data.database.RideHailDatabase
import com.example.ridehailing.data.database.dao.RideDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRideDatabase(@ApplicationContext context: Context): RideHailDatabase {
        return Room.databaseBuilder(
            context,
            RideHailDatabase::class.java,
            RideHailDatabase.DATABASE_NAME

        ).fallbackToDestructiveMigration()
            .build()

    }

    @Provides
    fun provideRideDao(database: RideHailDatabase): RideDao {
        return database.rideDao()
    }
}
