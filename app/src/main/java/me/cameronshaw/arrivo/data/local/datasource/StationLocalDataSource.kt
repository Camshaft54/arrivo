package me.cameronshaw.arrivo.data.local.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.arrivo.data.local.dao.StationDao
import me.cameronshaw.arrivo.data.local.model.StationEntity

class StationLocalDataSource(
    private val stationDao: StationDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun insertStation(station: StationEntity) =
        withContext(ioDispatcher) {
            stationDao.insertOrReplaceStation(station)
        }

    suspend fun updateStation(station: StationEntity) =
        withContext(ioDispatcher) {
            stationDao.updateStation(station)
        }

    suspend fun deleteStation(station: StationEntity) =
        withContext(ioDispatcher) {
            stationDao.deleteStation(station)
        }

    fun getAllStations() = stationDao.getAllStations()
}