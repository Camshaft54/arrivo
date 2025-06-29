package me.cameronshaw.amtraker.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.cameronshaw.amtraker.data.remote.api.MapOrEmptyArrayDeserializer
import me.cameronshaw.amtraker.data.remote.api.StationApiService
import me.cameronshaw.amtraker.data.remote.api.TrainApiService
import me.cameronshaw.amtraker.data.remote.dto.StationDto
import me.cameronshaw.amtraker.data.remote.dto.TrainDto
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideGson(): Gson {
        val trainMapType = object : TypeToken<Map<String, List<TrainDto>>>() {}.type
        val stationMapType = object : TypeToken<Map<String, List<StationDto>>>() {}.type

        return GsonBuilder()
            .registerTypeAdapter(
                trainMapType,
                MapOrEmptyArrayDeserializer(TrainDto::class.java)
            )
            .registerTypeAdapter(
                stationMapType,
                MapOrEmptyArrayDeserializer(StationDto::class.java)
            )
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("https://api-v3.amtraker.com/v3/")
        .client(
            OkHttpClient()
        )
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideTrainDao(retrofit: Retrofit): TrainApiService =
        retrofit.create(TrainApiService::class.java)

    @Provides
    @Singleton
    fun provideStationDao(retrofit: Retrofit): StationApiService =
        retrofit.create(StationApiService::class.java)

}