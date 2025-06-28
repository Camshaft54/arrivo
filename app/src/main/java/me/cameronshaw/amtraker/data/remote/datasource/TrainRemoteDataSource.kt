package me.cameronshaw.amtraker.data.remote.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.remote.api.TrainApiService
import me.cameronshaw.amtraker.data.remote.dto.TrainDto

class TrainRemoteDataSource(
    private val trainApi: TrainApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getTrain(trainId: String): TrainDto =
        withContext(ioDispatcher) {
            trainApi.getTrain(trainId)
        }
}