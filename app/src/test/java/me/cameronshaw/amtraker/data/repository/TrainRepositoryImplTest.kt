package me.cameronshaw.amtraker.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import me.cameronshaw.amtraker.data.local.datasource.TrainLocalDataSource
import me.cameronshaw.amtraker.data.remote.api.expected1TrainDomain
import me.cameronshaw.amtraker.data.remote.api.expected1TrainEntity
import me.cameronshaw.amtraker.data.remote.api.expected727TrainDomain
import me.cameronshaw.amtraker.data.remote.api.expected727TrainDto
import me.cameronshaw.amtraker.data.remote.api.expected727TrainEntity
import me.cameronshaw.amtraker.data.remote.datasource.TrainRemoteDataSource
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TrainRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var localDataSource: TrainLocalDataSource

    @RelaxedMockK
    private lateinit var remoteDataSource: TrainRemoteDataSource

    private lateinit var repository: TrainRepositoryImpl

    @Before
    fun setUp() {
        repository = TrainRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Test
    fun `refreshTrain WHEN remote source has data THEN it is saved to local source`() = runTest {
        // Arrange
        val fakeTrainId = "727"
        val fakeDto = expected727TrainDto
        coEvery { remoteDataSource.getTrain(fakeTrainId) } returns fakeDto

        // Act
        repository.refreshTrain(fakeTrainId)

        // Assert
        coVerify(exactly = 1) { remoteDataSource.getTrain(fakeTrainId) }
        coVerify(exactly = 1) { localDataSource.updateTrainData(any()) }
    }

    @Test
    fun `refreshTrain WHEN remote source is null THEN local source is NOT updated`() = runTest {
        // Arrange
        val fakeTrainId = "456"
        coEvery { remoteDataSource.getTrain(fakeTrainId) } returns null

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
        val resultFlow = repository.getTrackedTrains()

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