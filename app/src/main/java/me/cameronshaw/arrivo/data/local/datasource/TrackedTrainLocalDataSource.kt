package me.cameronshaw.arrivo.data.local.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.arrivo.data.local.dao.TrackedTrainDao
import me.cameronshaw.arrivo.data.local.model.TrackedTrainEntity

class TrackedTrainLocalDataSource(
    private val trackedTrainDao: TrackedTrainDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun insertTrackedTrain(trackedTrain: TrackedTrainEntity) =
        withContext(ioDispatcher) {
            trackedTrainDao.insertTrackedTrain(trackedTrain)
        }

    suspend fun deleteTrackedTrain(trackedTrain: TrackedTrainEntity) =
        withContext(ioDispatcher) {
            trackedTrainDao.deleteTrackedTrain(trackedTrain)
        }

    fun getTrackedTrains() =
            trackedTrainDao.getTrackedTrains()
}