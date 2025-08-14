package me.cameronshaw.amtraker.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.cameronshaw.amtraker.MainCoroutineRule
import me.cameronshaw.amtraker.data.local.AppDatabase
import me.cameronshaw.amtraker.data.local.model.StopEntity
import me.cameronshaw.amtraker.data.local.model.TrainEntity
import me.cameronshaw.amtraker.data.local.model.TrainWithStops
import me.cameronshaw.amtraker.data.util.toDbString
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.OffsetDateTime

@RunWith(AndroidJUnit4::class)
class TrainDaoTest {
    private lateinit var trainDao: TrainDao
    private lateinit var db: AppDatabase

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        trainDao = db.trainDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun createTestTrain(id: String, routeName: String) =
        TrainEntity(id, routeName, OffsetDateTime.now().toDbString())

    private fun TrainEntity.createTestStop(
        code: String,
        name: String,
        arrival: OffsetDateTime,
        departure: OffsetDateTime,
        scheduledArrival: OffsetDateTime? = null,
        scheduledDeparture: OffsetDateTime? = null
    ) = StopEntity(
        code,
        name,
        arrival.toDbString(),
        departure.toDbString(),
        scheduledArrival.toDbString(),
        scheduledDeparture.toDbString(),
        num
    )

    private val normalTrain1 = createTestTrain("101", "Test Route")

    private val normalStops1 =
        listOf(
            normalTrain1.createTestStop(
                "1",
                "Stop 1",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(1),
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(1)
            ),
            normalTrain1.createTestStop(
                "2",
                "Stop 2",
                OffsetDateTime.now().plusHours(2),
                OffsetDateTime.now().plusHours(3),
                OffsetDateTime.now().plusHours(2),
                OffsetDateTime.now().plusHours(3)
            )
        )

    private val normalTrainWithStops1 = TrainWithStops(normalTrain1, normalStops1)

    private val normalTrain2 = createTestTrain("102", "Test Route")

    private val normalStops2 =
        listOf(
            normalTrain2.createTestStop(
                "1",
                "Stop 1",
                OffsetDateTime.now(),
                OffsetDateTime.now().plusHours(1)
            ),
            normalTrain2.createTestStop(
                "2",
                "Stop 2",
                OffsetDateTime.now().plusHours(2),
                OffsetDateTime.now().plusHours(3)
            )
        )

    private val normalTrainWithStops2 = TrainWithStops(normalTrain2, normalStops2)

    @Test
    fun testInsertTrain_noRoute() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1.copy(routeName = ""))
        trainDao.insertOrReplaceStops(normalStops1)
        val withStops = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(
            withStops.first(),
            equalTo(normalTrainWithStops1.copy(train = normalTrain1.copy(routeName = "")))
        )
    }

    @Test
    fun testInsertTrain_duplicateStops() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        trainDao.insertOrReplaceTrain(normalTrain2)
        trainDao.insertOrReplaceStops(normalStops2)
        val withStops1 = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops1.first(), equalTo(normalTrainWithStops1))
        val withStops2 = trainDao.getTrainWithStops(normalTrain2.num)
        assertThat(withStops2.first(), equalTo(normalTrainWithStops2))
    }

    @Test
    fun testInsertTrain_duplicateTrain() = runTest {
        val duplicateNumTrain = createTestTrain("101", "Different Route")
        trainDao.insertOrReplaceTrain(duplicateNumTrain)
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        val withStops1 = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops1.first(), equalTo(normalTrainWithStops1))
    }

    @Test
    fun testInsertTrain_withoutStops() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        val withStops1 = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops1.first(), equalTo(TrainWithStops(normalTrain1, emptyList())))
    }

    @Test
    fun deleteStopsFromTrain_missingTrain() = runTest {
        trainDao.deleteStopsForTrain(normalTrain1.num)
    }

    @Test
    fun deleteStopsFromTrain_trainWithoutStops() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.deleteStopsForTrain(normalTrain1.num)
        val withStops1 = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops1.first(), equalTo(TrainWithStops(normalTrain1, emptyList())))
    }

    @Test
    fun deleteStopsFromTrain_normalTrain() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        trainDao.insertOrReplaceTrain(normalTrain2)
        trainDao.insertOrReplaceStops(normalStops2)
        trainDao.deleteStopsForTrain(normalTrain1.num)
        val withStops1 = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops1.first(), equalTo(TrainWithStops(normalTrain1, emptyList())))

        var withStops2 = trainDao.getTrainWithStops(normalTrain2.num)
        assertThat(withStops2.first(), equalTo(normalTrainWithStops2))

        trainDao.deleteStopsForTrain(normalTrain2.num)
        withStops2 = trainDao.getTrainWithStops(normalTrain2.num)
        assertThat(withStops2.first(), equalTo(TrainWithStops(normalTrain2, emptyList())))
    }

    @Test
    fun updateTrain_updatesTrainData() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        val updatedTrain = normalTrain1.copy(routeName = "Updated Route")
        trainDao.updateTrain(updatedTrain)
        val withStops = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops.first().train, equalTo(updatedTrain))
    }

    @Test
    fun deleteTrain_removesTrainAndStops() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        trainDao.deleteTrain(normalTrain1)
        val allTrains = trainDao.getAllTrains().first()
        assertThat(allTrains, empty())
        val stops = trainDao.getStopsByTrain(normalTrain1.num).first()
        assertThat(stops, empty())
    }

    @Test
    fun deleteAllTrains_removesAllTrainsAndStops() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        trainDao.insertOrReplaceTrain(normalTrain2)
        trainDao.insertOrReplaceStops(normalStops2)
        trainDao.deleteAllTrains()
        val allTrains = trainDao.getAllTrains().first()
        assertThat(allTrains, empty())
        val allTrainsWithStops = trainDao.getAllTrainsWithStops().first()
        assertThat(allTrainsWithStops, empty())
    }

    @Test
    fun updateTrainData_replacesTrainAndStops() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        val newStops = listOf(
            normalTrain1.createTestStop(
                "3",
                "Stop 3",
                OffsetDateTime.now().plusHours(4),
                OffsetDateTime.now().plusHours(5)
            ),
            normalTrain1.createTestStop(
                "4",
                "Stop 4",
                OffsetDateTime.now().plusHours(6),
                OffsetDateTime.now().plusHours(7)
            )
        )
        trainDao.updateTrainData(TrainWithStops(normalTrain1, newStops))
        val withStops = trainDao.getTrainWithStops(normalTrain1.num).first()
        assertThat(withStops.stops, equalTo(newStops))
    }

    @Test
    fun getAllTrains_returnsAllInsertedTrains() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceTrain(normalTrain2)
        val allTrains = trainDao.getAllTrains().first()
        assertThat(allTrains.sortedBy { it.num }, equalTo(listOf(normalTrain1, normalTrain2)))
    }

    @Test
    fun getAllTrainsWithStops_returnsAllTrainsWithTheirStops() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        trainDao.insertOrReplaceTrain(normalTrain2)
        trainDao.insertOrReplaceStops(normalStops2)
        val allTrainsWithStops = trainDao.getAllTrainsWithStops().first()
        assertThat(
            allTrainsWithStops.sortedBy { it.train.num },
            equalTo(listOf(normalTrainWithStops1, normalTrainWithStops2))
        )
    }
}