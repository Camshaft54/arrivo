package me.cameronshaw.amtraker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.cameronshaw.amtraker.data.local.model.StopEntity
import me.cameronshaw.amtraker.data.local.model.TrainEntity
import me.cameronshaw.amtraker.data.local.model.TrainWithStops

@Dao
interface TrainDao {
    @Insert
    fun insertTrain(train: TrainEntity)

    @Insert
    fun insertStops(stops: List<StopEntity>)

    @Update
    fun update(train: TrainEntity)

    @Delete
    fun delete(train: TrainEntity)

    @Query("SELECT * FROM TrainEntity")
    fun loadAll(): List<TrainEntity>

    @Transaction
    @Query("SELECT * FROM TrainEntity WHERE num = :num")
    fun loadWithStops(num: String): Flow<TrainWithStops>

}