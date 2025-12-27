package me.cameronshaw.arrivo.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.cameronshaw.arrivo.data.amtrak.api.AmtrakApiService
import me.cameronshaw.arrivo.data.amtrak.api.AmtrakDecryptionInterceptor
import me.cameronshaw.arrivo.data.amtrak.api.AmtrakDecryptor
import me.cameronshaw.arrivo.data.amtraker.api.AmtrakerApiService
import me.cameronshaw.arrivo.data.amtraker.api.MapOrEmptyArrayDeserializer
import me.cameronshaw.arrivo.data.amtraker.dto.StationDto
import me.cameronshaw.arrivo.data.amtraker.dto.TrainDto
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AmtrakRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AmtrakerRetrofit

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
    @AmtrakerRetrofit
    fun provideAmtrakerRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("https://api-v3.amtraker.com/v3/")
        .client(
            OkHttpClient()
        )
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    @AmtrakRetrofit
    fun provideAmtrakRetrofit(
        interceptor: AmtrakDecryptionInterceptor
    ): Retrofit {
        // You can add your custom decryption interceptor here
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://maps.amtrak.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAmtrakerDao(@AmtrakerRetrofit retrofit: Retrofit): AmtrakerApiService =
        retrofit.create(AmtrakerApiService::class.java)

    @Provides
    @Singleton
    fun provideAmtrakDao(@AmtrakRetrofit retrofit: Retrofit): AmtrakApiService =
        retrofit.create(AmtrakApiService::class.java)

    @Provides
    @Singleton
    fun provideAmtrakDecryptor(): AmtrakDecryptor {
        return AmtrakDecryptor()
    }

}