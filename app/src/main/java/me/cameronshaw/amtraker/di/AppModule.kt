package me.cameronshaw.amtraker.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.cameronshaw.amtraker.data.amtrakremote.api.AmtrakApiService
import me.cameronshaw.amtraker.data.amtrakremote.api.AmtrakDecryptor
import me.cameronshaw.amtraker.data.amtrakremote.datasource.AmtrakStationDataSource
import me.cameronshaw.amtraker.data.amtrakremote.datasource.AmtrakTrainDataSource
import me.cameronshaw.amtraker.data.local.dao.StationDao
import me.cameronshaw.amtraker.data.local.dao.TrainDao
import me.cameronshaw.amtraker.data.local.datasource.StationLocalDataSource
import me.cameronshaw.amtraker.data.local.datasource.TrainLocalDataSource
import me.cameronshaw.amtraker.data.remote.api.AmtrakerApiService
import me.cameronshaw.amtraker.data.remote.datasource.StationRemoteDataSource
import me.cameronshaw.amtraker.data.remote.datasource.TrainRemoteDataSource
import me.cameronshaw.amtraker.data.repository.ScheduleRepository
import me.cameronshaw.amtraker.data.repository.ScheduleRepositoryImpl
import me.cameronshaw.amtraker.data.repository.SettingsRepository
import me.cameronshaw.amtraker.data.repository.SettingsRepositoryImpl
import me.cameronshaw.amtraker.data.repository.StationRepository
import me.cameronshaw.amtraker.data.repository.StationRepositoryImpl
import me.cameronshaw.amtraker.data.repository.TrainRepository
import me.cameronshaw.amtraker.data.repository.TrainRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- DATA SOURCES ---

    @Provides
    @Singleton
    fun provideTrainLocalDataSource(trainDao: TrainDao): TrainLocalDataSource {
        return TrainLocalDataSource(trainDao)
    }

    @Provides
    @Singleton
    fun provideAmtrakerTrainDataSource(apiService: AmtrakerApiService): TrainRemoteDataSource {
        return TrainRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideAmtrakTrainDataSource(
        apiService: AmtrakApiService,
        decryptor: AmtrakDecryptor
    ): AmtrakTrainDataSource {
        return AmtrakTrainDataSource(apiService, decryptor)
    }

    @Provides
    @Singleton
    fun provideStationLocalDataSource(stationDao: StationDao): StationLocalDataSource {
        return StationLocalDataSource(stationDao)
    }

    @Provides
    @Singleton
    fun provideAmtrakerStationDataSource(apiService: AmtrakerApiService): StationRemoteDataSource {
        return StationRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideAmtrakStationDataSource(
        apiService: AmtrakApiService,
        decryptor: AmtrakDecryptor
    ): AmtrakStationDataSource {
        return AmtrakStationDataSource(apiService, decryptor)
    }


    // --- REPOSITORIES ---

    @Provides
    @Singleton
    fun provideTrainRepository(
        localDataSource: TrainLocalDataSource,
        remoteDataSource: TrainRemoteDataSource,
        amtrakTrainDataSource: AmtrakTrainDataSource,
        gson: Gson,
        settingsRepository: SettingsRepository
    ): TrainRepository {
        return TrainRepositoryImpl(localDataSource, remoteDataSource, amtrakTrainDataSource, gson, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideStationRepository(
        localDataSource: StationLocalDataSource,
        remoteDataSource: StationRemoteDataSource,
        amtrakDataSource: AmtrakStationDataSource,
        settingsRepository: SettingsRepository
    ): StationRepository {
        return StationRepositoryImpl(localDataSource, remoteDataSource, amtrakDataSource, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        trainRepository: TrainRepository,
        stationRepository: StationRepository
    ): ScheduleRepository {
        return ScheduleRepositoryImpl(trainRepository, stationRepository)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}