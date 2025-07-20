package me.cameronshaw.amtraker.data.repository

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.cameronshaw.amtraker.data.local.datasource.StationLocalDataSource
import me.cameronshaw.amtraker.data.local.model.toDomain
import me.cameronshaw.amtraker.data.model.Station
import me.cameronshaw.amtraker.data.model.toEntity
import me.cameronshaw.amtraker.data.remote.datasource.StationRemoteDataSource
import me.cameronshaw.amtraker.data.remote.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

interface StationRepository {
    /**
     * Fetches the latest data for a specific station from the remote source
     * and updates the local database.
     */
    suspend fun refreshStation(stationId: String)

    /**
     * Fetches all stations from the remote API, converts to the necessary entities, and saves it to the local database.
     */
    suspend fun refreshAllStations()

    /**
     * Gets a continuous stream of all stations from the local database.
     * The UI will observe this to get real-time updates.
     */
    fun getStations(): Flow<List<Station>>

    /**
     * Adds a new train to the local database. Assumes the domain model
     * is converted to the necessary entities for insertion.
     */
    suspend fun addStation(station: Station)

    /**
     * Updates an existing station in the local database.
     */
    suspend fun updateStation(station: Station)

    /**
     * Deletes a station from the local database.
     */
    suspend fun deleteStation(station: Station)
}

/**
 * The implementation of the repository. It coordinates the local and remote
 * data sources to fulfill the requests defined in the interface.
 *
 * @param remoteDataSource The data source for fetching data from the network.
 * @param localDataSource The data source for interacting with the local Room database.
 */
@Singleton
class StationRepositoryImpl @Inject constructor(
    private val localDataSource: StationLocalDataSource,
    private val remoteDataSource: StationRemoteDataSource
) : StationRepository {

    /**
     * Fetches a train from the remote API, converts it to a domain model,
     * then converts that to the necessary entities and saves it to the local database.
     */
    override suspend fun refreshStation(stationId: String) {
        val stationDto = remoteDataSource.getStation(stationId)
        if (stationDto != null) {
            val domainStation = stationDto.toDomain()
            val stationEntity = domainStation.toEntity()
            localDataSource.updateStation(stationEntity)
        }
    }

    /**
     * Fetches all stations from the remote API, converts to the necessary entities, and saves it to the local database.
     */
    override suspend fun refreshAllStations() {
        val trainsToRefresh = localDataSource.getAllStations().first()
        // TODO: change this to use the trains endpoint instead of each specific one
        coroutineScope {
            trainsToRefresh.map {
                async {
                    refreshStation(it.code)
                    Log.d("refreshAllStations", "Refreshed train: ${it.code}")
                }
            }
        }.awaitAll()
    }

    /**
     * Retrieves a Flow of train data from the local data source and maps
     * the list of entities to a list of clean domain models.
     */
    override fun getStations(): Flow<List<Station>> {
        return localDataSource.getAllStations().map { listOfStations ->
            listOfStations.map { it.toDomain() }
        }
    }

    /**
     * Converts a domain model to the entity representation and inserts it
     * into the local database.
     */
    override suspend fun addStation(station: Station) {
        val stationEntity = station.toEntity()
        localDataSource.insertStation(stationEntity)
    }

    /**
     * Converts a domain model to a core TrainEntity and calls the update
     * method in the local data source.
     */
    override suspend fun updateStation(station: Station) {
        val stationEntity = station.toEntity()
        localDataSource.updateStation(stationEntity)
    }

    /**
     * Converts a domain model to the entity representation that the delete
     * method in the local data source expects.
     */
    override suspend fun deleteStation(station: Station) {
        val stationEntity = station.toEntity()
        localDataSource.deleteStation(stationEntity)
    }
}