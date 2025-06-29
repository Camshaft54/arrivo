package me.cameronshaw.amtraker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.cameronshaw.amtraker.data.local.AppDatabase
import me.cameronshaw.amtraker.data.local.dao.StationDao
import me.cameronshaw.amtraker.data.local.dao.TrainDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "database"
        ).build()

    @Provides
    fun provideTrainDao(appDatabase: AppDatabase): TrainDao {
        return appDatabase.trainDao()
    }

    @Provides
    fun provideStationDao(appDatabase: AppDatabase): StationDao {
        return appDatabase.stationDao()
    }
}