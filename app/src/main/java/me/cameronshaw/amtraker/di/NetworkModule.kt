package me.cameronshaw.amtraker.di

import dagger.Module
import dagger.Provides
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


}