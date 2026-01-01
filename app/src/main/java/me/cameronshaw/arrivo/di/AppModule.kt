package me.cameronshaw.arrivo.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.cameronshaw.arrivo.data.amtrak.api.AmtrakApiService
import me.cameronshaw.arrivo.data.amtrak.api.AmtrakDecryptor
import me.cameronshaw.arrivo.data.amtrak.datasource.AmtrakStationDataSource
import me.cameronshaw.arrivo.data.amtrak.datasource.AmtrakTrainDataSource
import me.cameronshaw.arrivo.data.amtraker.api.AmtrakerApiService
import me.cameronshaw.arrivo.data.amtraker.datasource.StationAmtrakerDataSource
import me.cameronshaw.arrivo.data.amtraker.datasource.TrainAmtrakerDataSource
import me.cameronshaw.arrivo.data.local.dao.StationDao
import me.cameronshaw.arrivo.data.local.dao.TrackedTrainDao
import me.cameronshaw.arrivo.data.local.dao.TrainDao
import me.cameronshaw.arrivo.data.local.datasource.StationLocalDataSource
import me.cameronshaw.arrivo.data.local.datasource.TrackedTrainLocalDataSource
import me.cameronshaw.arrivo.data.local.datasource.TrainLocalDataSource
import me.cameronshaw.arrivo.data.repository.ScheduleRepository
import me.cameronshaw.arrivo.data.repository.ScheduleRepositoryImpl
import me.cameronshaw.arrivo.data.repository.SettingsRepository
import me.cameronshaw.arrivo.data.repository.SettingsRepositoryImpl
import me.cameronshaw.arrivo.data.repository.StationRepository
import me.cameronshaw.arrivo.data.repository.StationRepositoryImpl
import me.cameronshaw.arrivo.data.repository.TrackedTrainRepository
import me.cameronshaw.arrivo.data.repository.TrackedTrainRepositoryImpl
import me.cameronshaw.arrivo.data.repository.TrainRepository
import me.cameronshaw.arrivo.data.repository.TrainRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- DATA SOURCES ---

    @Provides
    @Singleton
    fun provideTrackedTrainLocalDataSource(trackedTrainDao: TrackedTrainDao): TrackedTrainLocalDataSource {
        return TrackedTrainLocalDataSource(trackedTrainDao)
    }

    @Provides
    @Singleton
    fun provideTrainLocalDataSource(trainDao: TrainDao): TrainLocalDataSource {
        return TrainLocalDataSource(trainDao)
    }

    @Provides
    @Singleton
    fun provideAmtrakerTrainDataSource(apiService: AmtrakerApiService): TrainAmtrakerDataSource {
        return TrainAmtrakerDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideAmtrakTrainDataSource(
        apiService: AmtrakApiService, decryptor: AmtrakDecryptor
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
    fun provideAmtrakerStationDataSource(apiService: AmtrakerApiService): StationAmtrakerDataSource {
        return StationAmtrakerDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideAmtrakStationDataSource(
        apiService: AmtrakApiService, decryptor: AmtrakDecryptor
    ): AmtrakStationDataSource {
        return AmtrakStationDataSource(apiService, decryptor)
    }


    // --- REPOSITORIES ---

    @Provides
    @Singleton
    fun provideTrainRepository(
        localDataSource: TrainLocalDataSource,
        remoteDataSource: TrainAmtrakerDataSource,
        amtrakTrainDataSource: AmtrakTrainDataSource,
        gson: Gson,
        settingsRepository: SettingsRepository,
        trackedTrainRepository: TrackedTrainRepository
    ): TrainRepository {
        return TrainRepositoryImpl(
            localDataSource,
            remoteDataSource,
            amtrakTrainDataSource,
            gson,
            settingsRepository,
            trackedTrainRepository
        )
    }

    @Provides
    @Singleton
    fun provideStationRepository(
        localDataSource: StationLocalDataSource,
        remoteDataSource: StationAmtrakerDataSource,
        amtrakDataSource: AmtrakStationDataSource,
        settingsRepository: SettingsRepository
    ): StationRepository {
        return StationRepositoryImpl(
            localDataSource, remoteDataSource, amtrakDataSource, settingsRepository
        )
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(
        trainRepository: TrainRepository, stationRepository: StationRepository
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

    @Provides
    @Singleton
    fun provideTrackedTrainRepository(
        localDataSource: TrackedTrainLocalDataSource
    ): TrackedTrainRepository {
        return TrackedTrainRepositoryImpl(localDataSource)
    }
}