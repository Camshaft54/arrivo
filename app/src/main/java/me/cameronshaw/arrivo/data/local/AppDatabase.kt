package me.cameronshaw.arrivo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import me.cameronshaw.arrivo.data.local.dao.StationDao
import me.cameronshaw.arrivo.data.local.dao.TrainDao
import me.cameronshaw.arrivo.data.local.model.StationEntity
import me.cameronshaw.arrivo.data.local.model.StopEntity
import me.cameronshaw.arrivo.data.local.model.TrainEntity

@Database(entities = [TrainEntity::class, StopEntity::class, StationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainDao(): TrainDao
    abstract fun stationDao(): StationDao
}