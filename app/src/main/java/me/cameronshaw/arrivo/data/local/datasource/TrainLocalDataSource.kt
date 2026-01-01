package me.cameronshaw.arrivo.data.local.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.arrivo.data.local.dao.TrainDao
import me.cameronshaw.arrivo.data.local.model.TrainEntity
import me.cameronshaw.arrivo.data.local.model.TrainWithStopsEntity

class TrainLocalDataSource(
    private val trainDao: TrainDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun insertTrain(train: TrainEntity) =
        withContext(ioDispatcher) {
            trainDao.insertOrReplaceTrain(train)
        }

    suspend fun insertTrainsWithStops(trainsWithStops: List<TrainWithStopsEntity>) =
        withContext(ioDispatcher) {
            trainDao.insertTrainsWithStops(trainsWithStops)
        }

    suspend fun updateTrainData(train: TrainWithStopsEntity) =
        withContext(ioDispatcher) {
            trainDao.updateTrainData(train)
        }

    suspend fun updateTrain(train: TrainEntity) =
        withContext(ioDispatcher) {
            trainDao.updateTrain(train)
        }

    suspend fun deleteTrain(train: TrainWithStopsEntity) =
        withContext(ioDispatcher) {
            trainDao.deleteTrain(train.train)
        }

    suspend fun deleteAllTrains() {
        withContext(ioDispatcher) {
            trainDao.deleteAllTrains()
        }
    }

    fun getTrainWithStops(id: String) = trainDao.getTrainWithStops(id)

    fun getAllTrains() = trainDao.getAllTrains()

    fun getAllTrainsWithStops() = trainDao.getAllTrainsWithStops()
}