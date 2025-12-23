package me.cameronshaw.amtraker.data.remote.api

import me.cameronshaw.amtraker.data.remote.dto.StationDto
import me.cameronshaw.amtraker.data.remote.dto.TrainDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AmtrakerApiService {
    @GET("stations/{stationId}")
    suspend fun getStation(@Path("stationId") stationId: String): Map<String, StationDto>

    @GET("trains/{trainId}")
    suspend fun getTrain(@Path("trainId") trainId: String): Map<String, List<TrainDto>>
}