package me.cameronshaw.amtraker.data.remote.api

import me.cameronshaw.amtraker.data.remote.dto.StationDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StationApiService {
    @GET("stations/{stationId}")
    suspend fun getStation(@Path("stationId") stationId: String): StationDto
}