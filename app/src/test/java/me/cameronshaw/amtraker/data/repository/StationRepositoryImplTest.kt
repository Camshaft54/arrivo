package me.cameronshaw.amtraker.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.cameronshaw.amtraker.data.local.datasource.StationLocalDataSource
import me.cameronshaw.amtraker.data.model.toEntity
import me.cameronshaw.amtraker.data.remote.api.expectedGACStationDomain
import me.cameronshaw.amtraker.data.remote.api.expectedGACStationDto
import me.cameronshaw.amtraker.data.remote.api.expectedGACStationEntity
import me.cameronshaw.amtraker.data.remote.api.expectedSJCStationDomain
import me.cameronshaw.amtraker.data.remote.api.expectedSJCStationEntity
import me.cameronshaw.amtraker.data.remote.datasource.StationRemoteDataSource
import me.cameronshaw.amtraker.data.remote.dto.toDomain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class StationRepositoryImplTest {

    // This rule automatically creates mock instances for annotated properties.
    @get:Rule
    val mockkRule = MockKRule(this)

    // Using @RelaxedMockK to avoid having to stub every single function call.
    @RelaxedMockK
    private lateinit var localDataSource: StationLocalDataSource

    @RelaxedMockK
    private lateinit var remoteDataSource: StationRemoteDataSource

    // The class we are actually testing.
    private lateinit var repository: StationRepositoryImpl

    @Before
    fun setUp() {
        // Create a new instance of the repository before each test,
        // injecting the mock data sources.
        repository = StationRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Test
    fun `refreshStation WHEN remote source has data THEN it is saved to local source`() = runTest {
        // Arrange: Define what our fakes should do.
        val fakeStationId = "GAC"
        val fakeDto = expectedGACStationDto
        coEvery { remoteDataSource.getStation(fakeStationId) } returns fakeDto

        // Act: Call the method we are testing.
        repository.refreshStation(fakeStationId)

        // Assert: Verify that the correct methods were called on our mocks.
        coVerify(exactly = 1) { remoteDataSource.getStation(fakeStationId) }
        // This test isn't comprehensive, but we can't just check for object equality due to lastUpdated field
        coVerify(exactly = 1) {
            localDataSource.updateStation(
                match { stationEntity ->
                    stationEntity.code == fakeDto.code && stationEntity.name == fakeDto.name
                })
        }
    }

    @Test
    fun `refreshStation WHEN remote source is null THEN local source is NOT updated`() = runTest {
        // Arrange: Define the failure case where the remote source finds nothing.
        val fakeStationId = "XYZ"
        coEvery { remoteDataSource.getStation(fakeStationId) } returns null

        // Act
        repository.refreshStation(fakeStationId)

        // Assert: Verify that the local data source was never called.
        coVerify(exactly = 0) { localDataSource.updateStation(any()) }
    }

    @Test
    fun `getStations THEN maps entities from local source to domain models`() = runTest {
        // Arrange
        val fakeStationEntities = listOf(
            expectedGACStationEntity, expectedSJCStationEntity
        )
        val expectedStations = listOf(
            expectedGACStationDomain, expectedSJCStationDomain
        )
        coEvery { localDataSource.getAllStations() } returns flowOf(fakeStationEntities)

        // Act
        val resultFlow = repository.getStations()

        // Assert: Use Turbine library to easily test Flow emissions.
        kotlinx.coroutines.test.runTest {
            assertEquals(expectedStations, resultFlow.first())
        }
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
