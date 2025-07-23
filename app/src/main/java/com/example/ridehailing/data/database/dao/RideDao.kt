package com.example.ridehailing.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.ridehailing.data.database.entities.EntityRide
import com.example.ridehailing.domain.model.RideStatus
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) for ride-related database operations
 */

@Dao
interface RideDao {

    @Query("SELECT * FROM rides ORDER BY requestTime DESC")
    fun getAllRides(): Flow<List<EntityRide>>

    @Query("SELECT * FROM rides WHERE id = :rideId")
    suspend fun getRideById(rideId: Long): EntityRide?

    @Query("SELECT * FROM rides WHERE status = :status ORDER BY requestTime DESC")
    fun getRidesByStatus(status: RideStatus): Flow<List<EntityRide>>

    @Query("SELECT * FROM rides WHERE status IN (:statuses) ORDER BY requestTime DESC")
    fun getRidesByStatuses(statuses: List<RideStatus>): Flow<List<EntityRide>>

    @Insert
    suspend fun insertRide(ride: EntityRide): Long

    @Update
    suspend fun updateRide(ride: EntityRide)

    @Delete
    suspend fun deleteRide(ride: EntityRide)

    @Query("DELETE FROM rides WHERE id = :rideId")
    suspend fun deleteRideById(rideId: Long)

    @Query("SELECT COUNT(*) FROM rides")
    suspend fun getRideCount(): Int

    @Query("SELECT * FROM rides WHERE requestTime >= :startDate AND requestTime <= :endDate ORDER BY requestTime DESC")
    suspend fun getRidesByDateRange(startDate: String, endDate: String): List<EntityRide>
}
