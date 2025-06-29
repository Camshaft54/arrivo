package me.cameronshaw.amtraker.data.remote.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.remote.api.StationApiService
import me.cameronshaw.amtraker.data.remote.dto.StationDto

class StationRemoteDataSource(
    private val stationApi: StationApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getStation(stationId: String): StationDto? =
        withContext(ioDispatcher) {
            val responseMap = stationApi.getStation(stationId)
            responseMap[stationId]
        }
}