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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

// TODO fix these tests once refreshAllTrains is fully switched away from refreshTrain()
@RunWith(Parameterized::class)
class TrainRepositoryImplTest(
    private val dataProvider: String
) {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var localDataSource: TrainLocalDataSource

    @RelaxedMockK
    private lateinit var remoteDataSource: TrainAmtrakerDataSource

    @RelaxedMockK
    private lateinit var amtrakDataSource: AmtrakTrainDataSource

    @RelaxedMockK
    private lateinit var gson: Gson

    @RelaxedMockK
    private lateinit var settingsRepository: SettingsRepositoryImpl

    private lateinit var repository: TrainRepositoryImpl

    @RelaxedMockK
    private lateinit var trackedTrainRepository: TrackedTrainRepositoryImpl

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Provider = {0}") // This names the test runs
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
    fun `refreshTrain WHEN remote source has data THEN it is saved to local source`() = runTest {
        // Arrange
        val fakeTrainId = "727 (2025-06-29T00:00:00-07:00)"
        val fakeDto = expected727TrainDto
        coEvery { remoteDataSource.getTrain(fakeTrainId) } returns fakeDto

        coEvery { trackedTrainRepository.getTrackedTrains() } returns flowOf(
            listOf(
                TrackedTrain(
                    num = fakeTrainId,
                    originDate = null
                )
            )
        )

        // Act
        repository.refreshAllTrains(fakeTrainId)

        // Assert
        coVerify(exactly = 1) { remoteDataSource.getTrain(fakeTrainId) }
        coVerify(exactly = 1) { localDataSource.updateTrainData(any()) }
    }

    @Test
    fun `refreshTrain WHEN remote source is null THEN local source is NOT updated`() = runTest {
        // Arrange
        val fakeTrainId = "456"
        coEvery { remoteDataSource.getTrain(fakeTrainId) } returns null

        coEvery { trackedTrainRepository.getTrackedTrains() } returns flowOf(emptyList())

        // Act
        repository.refreshTrain(fakeTrainId)

        // Assert
        coVerify(exactly = 0) { localDataSource.updateTrainData(any()) }
    }

    @Test
    fun `getTrackedTrains THEN maps entities from local source to domain models`() = runTest {
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
    fun `addTrain THEN calls updateTrainData on local source with mapped entity`() = runTest {
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