package com.example.ridehailing.data.database.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.ridehailing.domain.model.RideStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseTypeConverters {

    /**
     * Type converters for Room database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }

    @TypeConverter
    fun fromRideStatus(status: RideStatus): String {
        return status.name
    }

    @TypeConverter
    fun toRideStatus(status: String): RideStatus {
        return RideStatus.valueOf(status)
    }



}
