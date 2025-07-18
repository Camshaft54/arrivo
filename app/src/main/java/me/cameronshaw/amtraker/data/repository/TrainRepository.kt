package me.cameronshaw.amtraker.data.repository

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.cameronshaw.amtraker.data.local.datasource.TrainLocalDataSource
import me.cameronshaw.amtraker.data.local.model.toDomain
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.model.toEntity
import me.cameronshaw.amtraker.data.remote.datasource.TrainRemoteDataSource
import me.cameronshaw.amtraker.data.remote.dto.toDomain
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The repository interface defines the contract for data operations.
 * It abstracts the data sources from the rest of the app (e.g., ViewModels).
 */
interface TrainRepository {
    /**
     * Fetches the latest data for a specific train from the remote source
     * and updates the local database.
     */
    suspend fun refreshTrain(num: String)

    /**
     * Fetches all trains from the remote source and updates the local database.
     */
    suspend fun refreshAllTrains()

    /**
     * Gets a continuous stream of all trains from the local database.
     * The UI will observe this to get real-time updates.
     */
    fun getTrackedTrains(): Flow<List<Train>>

    /**
     * Adds a new train to the local database. Assumes the domain model
     * is converted to the necessary entities for insertion.
     */
    suspend fun addTrain(train: Train)

    /**
     * Updates an existing train's core data in the local database.
     */
    suspend fun updateTrain(train: Train)

    /**
     * Deletes a train and its associated data from the local database.
     */
    suspend fun deleteTrain(train: Train)
}

/**
 * The implementation of the repository. It coordinates the local and remote
 * data sources to fulfill the requests defined in the interface.
 *
 * @param remoteDataSource The data source for fetching data from the network.
 * @param localDataSource The data source for interacting with the local Room database.
 */
@Singleton
class TrainRepositoryImpl @Inject constructor(
    private val localDataSource: TrainLocalDataSource,
    private val remoteDataSource: TrainRemoteDataSource
) : TrainRepository {

    /**
     * Fetches a train from the remote API, converts it to a domain model,
     * then converts that to the necessary entities and saves it to the local database.
     */
    override suspend fun refreshTrain(num: String) {
        val trainDto = remoteDataSource.getTrain(num)
        if (trainDto != null) {
            val domainTrain =
                trainDto.toDomain() // TODO: handle situation where dep or arr are null
            val trainWithStopsEntity = domainTrain.toEntity()
            localDataSource.updateTrainData(trainWithStopsEntity)
        }
    }

    /**
     * Fetches all trains from the remote API, converts to the necessary entities, and saves it to the local database.
     */
    override suspend fun refreshAllTrains() {
        val trainsToRefresh = localDataSource.getAllTrains().first()
        // TODO: change this to use the trains endpoint instead of each specific one
        coroutineScope {
            trainsToRefresh.map {
                async {
                    refreshTrain(it.num)
                    Log.d("refreshAllTrains", "Refreshed train: ${it.num}")
                }
            }
        }.awaitAll()
    }

    /**
     * Retrieves a Flow of train data from the local data source and maps
     * the list of entities to a list of clean domain models.
     */
    override fun getTrackedTrains(): Flow<List<Train>> {
        return localDataSource.getAllTrainsWithStops().map { listOfTrainsWithStops ->
            listOfTrainsWithStops.map { it.toDomain() }
        }
    }

    /**
     * Converts a domain model to the entity representation and inserts it
     * into the local database.
     */
    override suspend fun addTrain(train: Train) {
        val trainEntity = train.toEntity().train
        localDataSource.insertTrain(trainEntity)
    }

    /**
     * Converts a domain model to a core TrainEntity and calls the update
     * method in the local data source.
     */
    override suspend fun updateTrain(train: Train) {
        val trainEntity = train.toEntity().train
        localDataSource.updateTrain(trainEntity)
    }

    /**
     * Converts a domain model to the entity representation that the delete
     * method in the local data source expects.
     */
    override suspend fun deleteTrain(train: Train) {
        val trainWithStopsEntity = train.toEntity()
        localDataSource.deleteTrain(trainWithStopsEntity)
    }
}