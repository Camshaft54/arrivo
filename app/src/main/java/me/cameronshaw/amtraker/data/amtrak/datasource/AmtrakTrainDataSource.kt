package me.cameronshaw.amtraker.data.amtrak.datasource

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.cameronshaw.amtraker.data.amtrak.api.AmtrakApiService
import me.cameronshaw.amtraker.data.amtrak.api.AmtrakDecryptor

class AmtrakTrainDataSource(
    private val api: AmtrakApiService,
    private val decryptor: AmtrakDecryptor,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getTrains(): List<Map<String, Any?>> =
        withContext(ioDispatcher) {
            if (!decryptor.hasKeys()) {
                val routeList = api.getRouteList()
                val routeListValues = api.getRouteListValues()
                decryptor.updateKeys(routeList, routeListValues)
            }
            api.getTrains().features.map { it.properties }
        }
}