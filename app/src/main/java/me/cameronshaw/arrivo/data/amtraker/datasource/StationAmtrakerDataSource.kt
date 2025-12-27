package me.cameronshaw.arrivo.data.amtraker.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.arrivo.data.amtraker.api.AmtrakerApiService
import me.cameronshaw.arrivo.data.amtraker.dto.StationDto

class StationAmtrakerDataSource(
    private val api: AmtrakerApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getStation(stationId: String): StationDto? =
        withContext(ioDispatcher) {
            val responseMap = api.getStation(stationId)
            responseMap[stationId]
        }
}