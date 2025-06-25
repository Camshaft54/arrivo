package me.cameronshaw.amtraker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import me.cameronshaw.amtraker.data.local.dao.StationDao
import me.cameronshaw.amtraker.data.local.dao.TrainDao
import me.cameronshaw.amtraker.data.local.model.StationEntity
import me.cameronshaw.amtraker.data.local.model.StopEntity
import me.cameronshaw.amtraker.data.local.model.TrainEntity

@Database(entities = [TrainEntity::class, StopEntity::class, StationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainDao(): TrainDao
    abstract fun stationDao(): StationDao
}