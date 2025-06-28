package me.cameronshaw.amtraker.di

import dagger.Module
import dagger.Provides
import me.cameronshaw.amtraker.data.remote.api.StationApiService
import me.cameronshaw.amtraker.data.remote.api.TrainApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api-v3.amtraker.com/v3/")
        .client(
            OkHttpClient()
        )
        .addConverterFactory(GsonConverterFactory.create())
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