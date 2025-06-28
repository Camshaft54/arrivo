package me.cameronshaw.amtraker.di

import dagger.Module
import dagger.Provides
import me.cameronshaw.amtraker.data.local.dao.StationDao
import me.cameronshaw.amtraker.data.local.dao.TrainDao
import me.cameronshaw.amtraker.data.remote.api.TrainApiService
import javax.inject.Singleton

@Module
object AppModule {

    // --- DATA SOURCES ---

    @Provides
    @Singleton
    fun provideTrainLocalDataSource(trainDao: TrainDao): TrainLocalDataSource {
        return TrainLocalDataSource(trainDao)
    }

    @Provides
    @Singleton
    fun provideTrainRemoteDataSource(apiService: TrainApiService): TrainRemoteDataSource {
        return TrainRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideStationLocalDataSource(stationDao: StationDao): StationLocalDataSource {
        return StationLocalDataSource(stationDao)
    }


    // --- REPOSITORIES ---

    @Provides
    @Singleton
    fun provideTrainRepository(
        localDataSource: TrainLocalDataSource,
        remoteDataSource: TrainRemoteDataSource
    ): TrainRepository {
        // Note: We provide the implementation, but return the interface type
        return TrainRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideStationRepository(
        localDataSource: StationLocalDataSource
    ): StationRepository {
        return StationRepositoryImpl(localDataSource)
    }
}