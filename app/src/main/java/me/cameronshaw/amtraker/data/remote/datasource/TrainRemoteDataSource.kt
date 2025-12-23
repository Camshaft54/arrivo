package me.cameronshaw.amtraker.data.remote.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.remote.api.AmtrakerApiService
import me.cameronshaw.amtraker.data.remote.dto.TrainDto

class TrainRemoteDataSource(
    private val api: AmtrakerApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getTrain(trainId: String): TrainDto? =
        withContext(ioDispatcher) {
            val responseMap = api.getTrain(trainId)
            responseMap[trainId]?.firstOrNull()
        }
}