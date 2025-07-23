package com.example.ridehailing.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ridehailing.data.database.dao.RideDao
import com.example.ridehailing.data.database.entities.EntityRide

/**
 * Room database class
 */
@Database(
    entities = [EntityRide::class],
    version = 1,
    exportSchema = false
)

abstract class RideHailDatabase : RoomDatabase() {

    abstract fun rideDao(): RideDao

    companion object {
        const val DATABASE_NAME = "ride_hailing_database"

        @Volatile
        private var INSTANCE: RideHailDatabase? = null

        fun getDatabase(context: Context): RideHailDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RideHailDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}



