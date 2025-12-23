package me.cameronshaw.amtraker.data.amtrakremote.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.amtrakremote.api.AmtrakApiService
import me.cameronshaw.amtraker.data.amtrakremote.api.AmtrakDecryptor
import me.cameronshaw.amtraker.data.amtrakremote.dto.AmtrakStationProperties
import javax.inject.Inject

class AmtrakStationDataSource @Inject constructor(
    private val api: AmtrakApiService,
    private val decryptor: AmtrakDecryptor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getStations(): List<AmtrakStationProperties> =
        withContext(ioDispatcher) {
            if (!decryptor.hasKeys()) {
                val routeList = api.getRouteList()
                val routeListValues = api.getRouteListValues()
                decryptor.updateKeys(routeList, routeListValues)
            }
            api.getStations().dataResponse.features.map { it.properties }
        }
}