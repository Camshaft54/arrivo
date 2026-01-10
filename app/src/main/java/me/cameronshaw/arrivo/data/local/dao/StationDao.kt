package me.cameronshaw.arrivo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.cameronshaw.arrivo.data.local.model.StationEntity

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

    @Query("SELECT * FROM StationEntity ORDER BY code")
    fun getAllStations(): Flow<List<StationEntity>>
}