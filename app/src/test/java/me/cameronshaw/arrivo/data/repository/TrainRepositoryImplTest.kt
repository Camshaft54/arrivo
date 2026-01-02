package me.cameronshaw.arrivo.data.repository

import android.util.Log
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.cameronshaw.arrivo.data.amtrak.datasource.AmtrakTrainDataSource
import me.cameronshaw.arrivo.data.amtraker.datasource.TrainAmtrakerDataSource
import me.cameronshaw.arrivo.data.local.datasource.TrainLocalDataSource
import me.cameronshaw.arrivo.data.local.model.AppSettings
import me.cameronshaw.arrivo.data.model.TrackedTrain
import me.cameronshaw.arrivo.data.remote.api.expected1TrainDomain
import me.cameronshaw.arrivo.data.remote.api.expected1TrainEntity
import me.cameronshaw.arrivo.data.remote.api.expected727TrainDomain
import me.cameronshaw.arrivo.data.remote.api.expected727TrainDto
import me.cameronshaw.arrivo.data.remote.api.expected727TrainEntity
import me.cameronshaw.arrivo.data.remote.api.expectedAmtrakApiTrains
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class TrainRepositoryImplTest(private val dataProvider: String) {
    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var localDataSource: TrainLocalDataSource

    @RelaxedMockK
    private lateinit var remoteDataSource: TrainAmtrakerDataSource

    @RelaxedMockK
    private lateinit var amtrakDataSource: AmtrakTrainDataSource

    private val gson: Gson = Gson()

    @RelaxedMockK
    private lateinit var settingsRepository: SettingsRepositoryImpl

    private lateinit var repository: TrainRepositoryImpl

    @RelaxedMockK
    private lateinit var trackedTrainRepository: TrackedTrainRepositoryImpl

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Provider = {0}")
        fun providers(): Collection<Array<Any>> {
            return listOf(
                arrayOf("AMTRAK"),
                arrayOf("AMTRAKER")
            )
        }
    }

    @Before
    fun setUp() {
        mockkStatic(Log::class)

        every { Log.d(any(), any()) } returns 0

        repository = TrainRepositoryImpl(
            localDataSource,
            remoteDataSource,
            amtrakDataSource,
            gson,
            settingsRepository,
            trackedTrainRepository
        )
        val mockAppSettings = AppSettings(dataProvider = dataProvider)
        coEvery { settingsRepository.appSettingsFlow() } returns flowOf(mockAppSettings)
    }

    @Test
    fun `refreshAllTrains WHEN remote source has data THEN it is saved to local source`() =
        runTest {
            // Arrange
            val trackedTrainId: String

            if (dataProvider == "AMTRAKER") {
                trackedTrainId = "727"
                coEvery { remoteDataSource.getTrains() } returns listOf(expected727TrainDto)
            } else { // AMTRAK
                trackedTrainId = "1"
                coEvery { amtrakDataSource.getTrains() } returns expectedAmtrakApiTrains
            }

            coEvery { trackedTrainRepository.getTrackedTrains() } returns flowOf(
                listOf(TrackedTrain(num = trackedTrainId, originDate = null))
            )

            // Act
            repository.refreshAllTrains()

            // Assert
            if (dataProvider == "AMTRAKER") {
                coVerify(exactly = 1) { remoteDataSource.getTrains() }
                coVerify(exactly = 0) { amtrakDataSource.getTrains() }
            } else { // AMTRAK
                coVerify(exactly = 1) { amtrakDataSource.getTrains() }
                coVerify(exactly = 0) { remoteDataSource.getTrains() }
            }
            coVerify(exactly = 1) { localDataSource.insertTrainsWithStops(any()) }
        }

    @Test
    fun `refreshAllTrains WHEN remote source is null THEN local source is NOT updated`() = runTest {
        // Arrange
        val fakeTrainId = "456"

        if (dataProvider == "AMTRAKER") {
            coEvery { remoteDataSource.getTrains() } returns emptyList()
        } else {
            coEvery { amtrakDataSource.getTrains() } returns emptyList()
        }

        coEvery { trackedTrainRepository.getTrackedTrains() } returns flowOf(
            listOf(TrackedTrain(num = fakeTrainId, originDate = null))
        )

        // Act
        repository.refreshAllTrains()

        // Assert
        if (dataProvider == "AMTRAKER") {
            coVerify(exactly = 1) { remoteDataSource.getTrains() }
            coVerify(exactly = 0) { amtrakDataSource.getTrains() }
        } else {
            coVerify(exactly = 1) { amtrakDataSource.getTrains() }
            coVerify(exactly = 0) { remoteDataSource.getTrains() }
        }
        coVerify(exactly = 0) { localDataSource.updateTrainData(any()) }
    }

    @Test
    fun `refreshAllTrains WHEN no tracked trains THEN remote data sources are not called`() = runTest {
        // Arrange
        coEvery { trackedTrainRepository.getTrackedTrains() } returns flowOf(emptyList())

        // Act
        repository.refreshAllTrains()

        // Assert
        coVerify(exactly = 1) { trackedTrainRepository.getTrackedTrains() }

        coVerify(exactly = 0) { remoteDataSource.getTrains() }
        coVerify(exactly = 0) { amtrakDataSource.getTrains() }
        coVerify(exactly = 0) { localDataSource.updateTrainData(any()) }
    }

    @Test
    fun `getTrains THEN maps entities from local source to domain models`() = runTest {
        // Arrange
        val fakeTrainEntities = listOf(
            expected727TrainEntity,
            expected1TrainEntity
        )
        val expectedTrains = listOf(
            expected727TrainDomain,
            expected1TrainDomain
        )
        coEvery { localDataSource.getAllTrainsWithStops() } returns flowOf(fakeTrainEntities)

        // Act
        val resultFlow = repository.getTrains()

        // Assert
        assertEquals(expectedTrains, resultFlow.first())
    }

    @Test
    fun `addTrain THEN calls insertTrain on local source with mapped entity`() = runTest {
        // Arrange
        val trainToInsert = expected727TrainDomain
        val expectedEntity = expected727TrainEntity.train

        // Act
        repository.addTrain(trainToInsert)

        // Assert
        coVerify(exactly = 1) { localDataSource.insertTrain(expectedEntity) }
    }

    @Test
    fun `updateTrain THEN calls updateTrain on local source with mapped entity`() = runTest {
        // Arrange
        val trainToUpdate = expected727TrainDomain
        val expectedEntity = expected727TrainEntity.train

        // Act
        repository.updateTrain(trainToUpdate)

        // Assert
        coVerify(exactly = 1) { localDataSource.updateTrain(expectedEntity) }
    }

    @Test
    fun `deleteTrain THEN calls delete on local source with mapped entity`() = runTest {
        // Arrange
        val trainToDelete = expected727TrainDomain
        val expectedEntity = expected727TrainEntity

        // Act
        repository.deleteTrain(trainToDelete)

        // Assert
        coVerify(exactly = 1) { localDataSource.deleteTrain(expectedEntity) }
    }
}
