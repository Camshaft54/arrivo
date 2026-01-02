package me.cameronshaw.arrivo.data.repository

import android.util.Log
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.cameronshaw.arrivo.data.amtrak.datasource.AmtrakStationDataSource
import me.cameronshaw.arrivo.data.amtraker.datasource.StationAmtrakerDataSource
import me.cameronshaw.arrivo.data.local.datasource.StationLocalDataSource
import me.cameronshaw.arrivo.data.local.model.AppSettings
import me.cameronshaw.arrivo.data.remote.api.expectedAmtrakApiStations
import me.cameronshaw.arrivo.data.remote.api.expectedGACStationDomain
import me.cameronshaw.arrivo.data.remote.api.expectedGACStationDto
import me.cameronshaw.arrivo.data.remote.api.expectedGACStationEntity
import me.cameronshaw.arrivo.data.remote.api.expectedSJCStationDomain
import me.cameronshaw.arrivo.data.remote.api.expectedSJCStationEntity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class StationRepositoryImplTest(
    private val dataProvider: String
) {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var localDataSource: StationLocalDataSource

    @RelaxedMockK
    private lateinit var remoteDataSource: StationAmtrakerDataSource

    @RelaxedMockK
    private lateinit var amtrakDataSource: AmtrakStationDataSource

    @RelaxedMockK
    private lateinit var settingsRepository: SettingsRepositoryImpl

    private lateinit var repository: StationRepositoryImpl

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

        repository = StationRepositoryImpl(localDataSource, remoteDataSource, amtrakDataSource, settingsRepository)
        // Mock the settings repository to return the provider for the current test run
        val mockAppSettings = AppSettings(dataProvider = dataProvider)
        coEvery { settingsRepository.appSettingsFlow() } returns flowOf(mockAppSettings)
    }

    @Test
    fun `refreshStation WHEN remote source has data THEN it is saved to local source`() = runTest {
        // Arrange
        val fakeStationId = "GAC"
        val fakeDto = expectedGACStationDto
        // For AMTRAK provider, refreshStation triggers refreshAllStations, so we need to mock the list
        coEvery { localDataSource.getAllStations() } returns flowOf(listOf(expectedGACStationEntity))
        coEvery { amtrakDataSource.getStations() } returns expectedAmtrakApiStations
        coEvery { remoteDataSource.getStation(fakeStationId) } returns fakeDto

        // Act
        repository.refreshStation(fakeStationId)

        // Assert
        if (dataProvider == "AMTRAKER") {
            coVerify(exactly = 1) { remoteDataSource.getStation(fakeStationId) }
            coVerify(exactly = 0) { amtrakDataSource.getStations() }
            coVerify(exactly = 1) { localDataSource.updateStation(any()) }
        } else { // AMTRAK
            coVerify(exactly = 0) { remoteDataSource.getStation(any()) }
            coVerify(exactly = 1) { amtrakDataSource.getStations() }
            coVerify(exactly = 1) { localDataSource.updateStation(any()) }
        }
    }

    @Test
    fun `refreshStation WHEN remote source is null THEN local source is NOT updated`() = runTest {
        // Arrange
        val fakeStationId = "XYZ"
        coEvery { remoteDataSource.getStation(fakeStationId) } returns null
        // For AMTRAK provider, ensure getStations returns an empty list for this test
        coEvery { amtrakDataSource.getStations() } returns expectedAmtrakApiStations
        coEvery { localDataSource.getAllStations() } returns flowOf(listOf(expectedGACStationEntity.copy(code = fakeStationId)))


        // Act
        repository.refreshStation(fakeStationId)

        // Assert
        if (dataProvider == "AMTRAKER") {
            coVerify(exactly = 1) { remoteDataSource.getStation(fakeStationId) }
        } else { // AMTRAK
            coVerify(exactly = 1) { amtrakDataSource.getStations() }
        }
        // In both cases, no station should be updated
        coVerify(exactly = 0) { localDataSource.updateStation(any()) }
    }

    @Test
    fun `refreshAllStations WHEN local stations exist THEN remote sources are called`() = runTest {
        // Arrange
        val fakeStationId = "GAC"
        val fakeDto = expectedGACStationDto
        val fakeEntity = expectedGACStationEntity
        coEvery { localDataSource.getAllStations() } returns flowOf(listOf(fakeEntity))
        coEvery { amtrakDataSource.getStations() } returns expectedAmtrakApiStations
        coEvery { remoteDataSource.getStation(fakeStationId) } returns fakeDto

        // Act
        repository.refreshAllStations()

        // Assert
        if (dataProvider == "AMTRAKER") {
            coVerify(exactly = 1) { remoteDataSource.getStation(fakeStationId) }
            coVerify(exactly = 0) { amtrakDataSource.getStations() }
        } else {
            coVerify(exactly = 0) { remoteDataSource.getStation(any()) }
            coVerify(exactly = 1) { amtrakDataSource.getStations() }
        }
        coVerify(exactly = 1) { localDataSource.updateStation(any()) }
    }

    @Test
    fun `refreshAllStations WHEN no local stations THEN remote sources are NOT called`() = runTest {
        // Arrange
        coEvery { localDataSource.getAllStations() } returns flowOf(emptyList())

        // Act
        repository.refreshAllStations()

        // Assert
        coVerify(exactly = 1) { localDataSource.getAllStations() }
        coVerify(exactly = 0) { remoteDataSource.getStation(any()) }
        coVerify(exactly = 0) { amtrakDataSource.getStations() }
        coVerify(exactly = 0) { localDataSource.updateStation(any()) }
    }

    @Test
    fun `getStations THEN maps entities from local source to domain models`() = runTest {
        // Arrange
        val fakeStationEntities = listOf(
            expectedGACStationEntity,
            expectedSJCStationEntity
        )
        val expectedStations = listOf(
            expectedGACStationDomain,
            expectedSJCStationDomain
        )
        coEvery { localDataSource.getAllStations() } returns flowOf(fakeStationEntities)

        // Act
        val resultFlow = repository.getStations()

        // Assert
        assertEquals(expectedStations, resultFlow.first())
    }

    @Test
    fun `addStation THEN calls insert on local source with mapped entity`() = runTest {
        // Arrange
        val stationToInsert = expectedGACStationDomain
        val expectedEntity = expectedGACStationEntity

        // Act
        repository.addStation(stationToInsert)

        // Assert
        coVerify(exactly = 1) { localDataSource.insertStation(expectedEntity) }
    }

    @Test
    fun `updateStation THEN calls update on local source with mapped entity`() = runTest {
        // Arrange
        val stationToUpdate = expectedSJCStationDomain
        val expectedEntity = expectedSJCStationEntity

        // Act
        repository.updateStation(stationToUpdate)

        // Assert
        coVerify(exactly = 1) { localDataSource.updateStation(expectedEntity) }
    }

    @Test
    fun `deleteStation THEN calls delete on local source with mapped entity`() = runTest {
        // Arrange
        val stationToDelete = expectedGACStationDomain
        val expectedEntity = expectedGACStationEntity

        // Act
        repository.deleteStation(stationToDelete)

        // Assert
        coVerify(exactly = 1) { localDataSource.deleteStation(expectedEntity) }
    }
}
