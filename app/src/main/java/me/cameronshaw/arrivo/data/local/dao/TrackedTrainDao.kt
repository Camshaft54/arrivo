package me.cameronshaw.arrivo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.cameronshaw.arrivo.data.local.model.TrackedTrainEntity

@Dao
interface TrackedTrainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackedTrain(trackedTrain: TrackedTrainEntity)

    @Delete
    suspend fun deleteTrackedTrain(trackedTrain: TrackedTrainEntity)

    @Query("SELECT * FROM TrackedTrainEntity ORDER BY num, originDate ASC")
    fun getTrackedTrains(): Flow<List<TrackedTrainEntity>>
}