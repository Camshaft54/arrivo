package me.cameronshaw.arrivo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.cameronshaw.arrivo.data.local.datasource.TrackedTrainLocalDataSource
import me.cameronshaw.arrivo.data.local.model.toDomain
import me.cameronshaw.arrivo.data.model.TrackedTrain
import me.cameronshaw.arrivo.data.model.toEntity
import javax.inject.Inject
import javax.inject.Singleton

interface TrackedTrainRepository {
    suspend fun insertTrackedTrain(trackedTrain: TrackedTrain)

    suspend fun deleteTrackedTrain(trackedTrain: TrackedTrain)

    fun getTrackedTrains(): Flow<List<TrackedTrain>>
}

@Singleton
class TrackedTrainRepositoryImpl @Inject constructor(
    private val localDataSource: TrackedTrainLocalDataSource
) : TrackedTrainRepository {
    override suspend fun insertTrackedTrain(trackedTrain: TrackedTrain) {
        localDataSource.insertTrackedTrain(trackedTrain.toEntity())
    }

    override suspend fun deleteTrackedTrain(trackedTrain: TrackedTrain) {
        localDataSource.deleteTrackedTrain(trackedTrain.toEntity())
    }

    override fun getTrackedTrains(): Flow<List<TrackedTrain>> {
        return localDataSource.getTrackedTrains().map { listOfTrackedTrains ->
            listOfTrackedTrains.map { it.toDomain() }
        }
    }
}