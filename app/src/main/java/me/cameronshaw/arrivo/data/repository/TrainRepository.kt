package me.cameronshaw.arrivo.data.repository

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.cameronshaw.arrivo.data.amtrak.datasource.AmtrakTrainDataSource
import me.cameronshaw.arrivo.data.amtrak.dto.toTrainDomain
import me.cameronshaw.arrivo.data.amtraker.datasource.TrainAmtrakerDataSource
import me.cameronshaw.arrivo.data.amtraker.dto.toDomain
import me.cameronshaw.arrivo.data.local.datasource.TrainLocalDataSource
import me.cameronshaw.arrivo.data.local.model.toDomain
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.data.model.toEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The repository interface defines the contract for data operations.
 * It abstracts the data sources from the rest of the app (e.g., ViewModels).
 */
interface TrainRepository {
    /**
     * Fetches all trains from the remote source and updates the local database.
     */
    suspend fun refreshAllTrains()

    /**
     * Gets a continuously updated train from the local database.
     */
    fun getTrain(id: String): Flow<Train>

    /**
     * Gets a continuous stream of all trains from the local database.
     * The UI will observe this to get real-time updates.
     */
    fun getTrains(): Flow<List<Train>>

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
 * @param amtrakerDataSource The data source for fetching data from the network.
 * @param localDataSource The data source for interacting with the local Room database.
 */
@Singleton
class TrainRepositoryImpl @Inject constructor(
    private val localDataSource: TrainLocalDataSource,
    private val amtrakerDataSource: TrainAmtrakerDataSource,
    private val amtrakDataSource: AmtrakTrainDataSource,
    private val gson: Gson,
    private val settingsRepository: SettingsRepository,
    private val trackedTrainRepository: TrackedTrainRepository
) : TrainRepository {

    private suspend fun useAmtrakApi() =
        when (settingsRepository.appSettingsFlow().first().dataProvider) {
            "AMTRAK" -> true
            "AMTRAKER" -> false
            else -> true
        }

    /**
     * Fetches all trains from the remote API, converts to the necessary entities, and saves it to the local database.
     */
    override suspend fun refreshAllTrains() {
        val trackedTrains = trackedTrainRepository.getTrackedTrains().first()

        val allRemoteTrains = if (useAmtrakApi()) {
            amtrakDataSource.getTrains().map { it.toTrainDomain(gson) }
        } else {
            amtrakerDataSource.getTrains().map { it.toDomain() }
        }

        val remoteTrainsByNum = allRemoteTrains.groupBy { it.num }

        val trainsToKeep = trackedTrains.flatMap { tracked ->
            val sameNum = remoteTrainsByNum[tracked.num] ?: emptyList()
            if (tracked.originDate == null) {
                sameNum
            } else {
                sameNum.filter { it.originDate == tracked.originDate }
            }
        }

        localDataSource.deleteAllTrains()
        localDataSource.insertTrainsWithStops(trainsToKeep.map { it.toEntity() })

        trainsToKeep.forEach {
            Log.d("refreshAllTrains", "Refreshed train: ${it.num}")
        }
    }

    /**
     * Gets a continuously updated train from the local database.
     */
    override fun getTrain(id: String): Flow<Train> {
        return localDataSource.getTrainWithStops(id).map { it.toDomain() }
    }

    /**
     * Retrieves a Flow of train data from the local data source and maps
     * the list of entities to a list of clean domain models.
     */
    override fun getTrains(): Flow<List<Train>> {
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