package me.cameronshaw.amtraker.data.remote.api

import me.cameronshaw.amtraker.data.remote.dto.TrainDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TrainApiService {
    @GET("trains/{trainId}")
    suspend fun getTrain(@Path("trainId") trainId: String): TrainDto
}