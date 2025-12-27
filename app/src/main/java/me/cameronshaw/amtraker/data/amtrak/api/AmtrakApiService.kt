package me.cameronshaw.amtraker.data.amtrak.api

import me.cameronshaw.amtraker.data.amtrak.dto.AmtrakStationRootResponse
import me.cameronshaw.amtraker.data.amtrak.dto.AmtrakTrainRootResponse
import me.cameronshaw.amtraker.data.amtrak.dto.RoutesListEntry
import me.cameronshaw.amtraker.data.amtrak.dto.RoutesListValuesDto
import retrofit2.http.GET
import retrofit2.http.Headers

interface AmtrakApiService {
    @Headers("X-Requires-Decryption: true")
    @GET("services/MapDataService/stations/trainStations")
    suspend fun getStations(): AmtrakStationRootResponse

    @Headers("X-Requires-Decryption: true")
    @GET("services/MapDataService/trains/getTrainsData")
    suspend fun getTrains(): AmtrakTrainRootResponse

    @GET("rttl/js/RoutesList.json")
    suspend fun getRouteList(): Array<RoutesListEntry>

    @GET("rttl/js/RoutesList.v.json")
    suspend fun getRouteListValues(): RoutesListValuesDto
}