package me.cameronshaw.amtraker.data.remote.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.remote.api.AmtrakerApiService
import me.cameronshaw.amtraker.data.remote.dto.StationDto

class StationRemoteDataSource(
    private val api: AmtrakerApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getStation(stationId: String): StationDto? =
        withContext(ioDispatcher) {
            val responseMap = api.getStation(stationId)
            responseMap[stationId]
        }
}