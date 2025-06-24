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
    suspend fun deleteTrain(train: TrainEntity)

    @Query("DELETE FROM StopEntity WHERE trainOwnerNum = :trainNum")
    suspend fun deleteStopsForTrain(trainNum: String)

    /**
     * To be used when fetching train data from remote
     */
    @Transaction
    suspend fun updateTrainData(train: TrainEntity, stops: List<StopEntity>) {
        insertOrReplaceTrain(train)
        deleteStopsForTrain(train.num)
        insertOrReplaceStops(stops)
    }

    /**
     * For UI to load all the trains the user has
     */
    @Query("SELECT * FROM TrainEntity ORDER BY num ASC")
    fun loadAll(): Flow<List<TrainEntity>>

    /**
     * For UI to load detailed view of a train
     */
    @Transaction
    @Query("SELECT * FROM TrainEntity WHERE num = :num")
    fun loadWithStops(num: String): Flow<TrainWithStops>

    /**
     * For widget to load all trains
     */
    @Transaction
    @Query("SELECT * FROM TrainEntity ORDER BY num ASC")
    fun loadAllWithStops(): Flow<List<TrainWithStops>>
}