package me.cameronshaw.amtraker.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.cameronshaw.amtraker.data.local.dao.StationDao
import me.cameronshaw.amtraker.data.local.dao.TrainDao
import me.cameronshaw.amtraker.data.local.datasource.StationLocalDataSource
import me.cameronshaw.amtraker.data.local.datasource.TrainLocalDataSource
import me.cameronshaw.amtraker.data.remote.api.StationApiService
import me.cameronshaw.amtraker.data.remote.api.TrainApiService
import me.cameronshaw.amtraker.data.remote.datasource.StationRemoteDataSource
import me.cameronshaw.amtraker.data.remote.datasource.TrainRemoteDataSource
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
    fun provideTrainRemoteDataSource(apiService: TrainApiService): TrainRemoteDataSource {
        return TrainRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideStationLocalDataSource(stationDao: StationDao): StationLocalDataSource {
        return StationLocalDataSource(stationDao)
    }

    @Provides
    @Singleton
    fun provideStationRemoteDataSource(apiService: StationApiService): StationRemoteDataSource {
        return StationRemoteDataSource(apiService)
    }


    // --- REPOSITORIES ---

//    @Provides
//    @Singleton
//    fun provideTrainRepository(
//        localDataSource: TrainLocalDataSource,
//        remoteDataSource: TrainRemoteDataSource
//    ): TrainRepository {
//        // Note: We provide the implementation, but return the interface type
//        return TrainRepositoryImpl(localDataSource, remoteDataSource)
//    }
//
//    @Provides
//    @Singleton
//    fun provideStationRepository(
//        localDataSource: StationLocalDataSource
//    ): StationRepository {
//        return StationRepositoryImpl(localDataSource)
//    }
}