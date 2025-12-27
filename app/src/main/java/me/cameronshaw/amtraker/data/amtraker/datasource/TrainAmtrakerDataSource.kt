package me.cameronshaw.amtraker.data.amtraker.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.amtraker.api.AmtrakerApiService
import me.cameronshaw.amtraker.data.amtraker.dto.TrainDto

class TrainAmtrakerDataSource(
    private val api: AmtrakerApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getTrain(trainId: String): TrainDto? =
        withContext(ioDispatcher) {
            val responseMap = api.getTrain(trainId)
            responseMap[trainId]?.firstOrNull()
        }
}