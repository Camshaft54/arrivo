package me.cameronshaw.amtraker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.cameronshaw.amtraker.data.local.model.StationEntity

@Dao
interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceStation(station: StationEntity)

    @Update
    suspend fun updateStation(station: StationEntity)

    @Delete
    suspend fun deleteStation(station: StationEntity)

    @Query("SELECT * FROM StationEntity WHERE code = :code")
    fun getStationByCode(code: String): Flow<StationEntity>

    @Query("SELECT * FROM StationEntity")
    fun getAllStations(): Flow<List<StationEntity>>
}