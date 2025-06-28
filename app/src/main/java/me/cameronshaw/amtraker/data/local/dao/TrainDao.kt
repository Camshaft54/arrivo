package me.cameronshaw.amtraker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.cameronshaw.amtraker.data.local.model.StopEntity
import me.cameronshaw.amtraker.data.local.model.TrainEntity
import me.cameronshaw.amtraker.data.local.model.TrainWithStops

@Dao
interface TrainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceTrain(train: TrainEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceStops(stops: List<StopEntity>)

    /**
     * To be used for cases in UI where we want to set something
     * about the train (e.g. favorite it)
     */
    @Update
    suspend fun updateTrain(train: TrainEntity)

    /**
     * This method will automatically delete stops associated
     * with the train (See StopEntity)
     */
    @Delete
    @Transaction
    suspend fun deleteTrain(train: TrainEntity)

    @Query("DELETE FROM StopEntity WHERE trainOwnerNum = :trainNum")
    suspend fun deleteStopsForTrain(trainNum: String)

    @Query("DELETE FROM TrainEntity")
    suspend fun deleteAllTrains()

    /**
     * To be used when updating a single train's data from remote
     */
    @Transaction
    suspend fun updateTrainData(train: TrainWithStops) {
        insertOrReplaceTrain(train.train)
        deleteStopsForTrain(train.train.num)
        insertOrReplaceStops(train.stops)
    }

    /**
     * For UI to load all the trains the user has
     */
    @Query("SELECT * FROM TrainEntity ORDER BY num ASC")
    fun getAllTrains(): Flow<List<TrainEntity>>

    /**
     * For UI to load detailed view of a train
     */
    @Transaction
    @Query("SELECT * FROM TrainEntity WHERE num = :num")
    fun getTrainWithStops(num: String): Flow<TrainWithStops>

    /**
     * For widget to load all trains
     */
    @Transaction
    @Query("SELECT * FROM TrainEntity ORDER BY num ASC")
    fun getAllTrainsWithStops(): Flow<List<TrainWithStops>>

    @Query("SELECT * FROM StopEntity WHERE trainOwnerNum = :trainNum ORDER BY code ASC") // Example ordering
    fun getStopsByTrain(trainNum: String): Flow<List<StopEntity>>
}