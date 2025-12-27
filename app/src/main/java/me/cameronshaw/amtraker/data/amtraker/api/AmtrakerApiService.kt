package me.cameronshaw.amtraker.data.amtraker.api

import me.cameronshaw.amtraker.data.amtraker.dto.StationDto
import me.cameronshaw.amtraker.data.amtraker.dto.TrainDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AmtrakerApiService {
    @GET("stations/{stationId}")
    suspend fun getStation(@Path("stationId") stationId: String): Map<String, StationDto>

    @GET("trains/{trainId}")
    suspend fun getTrain(@Path("trainId") trainId: String): Map<String, List<TrainDto>>
}