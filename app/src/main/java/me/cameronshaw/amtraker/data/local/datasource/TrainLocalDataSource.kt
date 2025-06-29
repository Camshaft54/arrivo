package me.cameronshaw.amtraker.data.local.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.local.dao.TrainDao
import me.cameronshaw.amtraker.data.local.model.TrainEntity
import me.cameronshaw.amtraker.data.local.model.TrainWithStops

class TrainLocalDataSource(
    private val trainDao: TrainDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun insertTrain(train: TrainEntity) =
        withContext(ioDispatcher) {
            trainDao.insertOrReplaceTrain(train)
        }

    suspend fun updateTrainData(train: TrainWithStops) =
        withContext(ioDispatcher) {
            trainDao.updateTrainData(train)
        }

    suspend fun updateTrain(train: TrainEntity) =
        withContext(ioDispatcher) {
            trainDao.updateTrain(train)
        }

    suspend fun deleteTrain(train: TrainWithStops) =
        withContext(ioDispatcher) {
            trainDao.deleteTrain(train.train)
        }

    suspend fun deleteAllTrains() {
        withContext(ioDispatcher) {
            trainDao.deleteAllTrains()
        }
    }

    fun getAllTrains() = trainDao.getAllTrains()

    fun getAllTrainsWithStops() = trainDao.getAllTrainsWithStops()
}