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
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

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

    private fun createTestTrain(id: String, routeName: String) = TrainEntity(id, routeName)

    private fun TrainEntity.createTestStop(code: String, name: String) = StopEntity(code, name, num)

    private val normalTrain1 = createTestTrain("101", "Test Route")

    private val normalStops1 =
        listOf(normalTrain1.createTestStop("1", "Stop 1"),
            normalTrain1.createTestStop("2", "Stop 2"))

    private val normalTrainWithStops1 = TrainWithStops(normalTrain1, normalStops1)

    private val normalTrain2 = createTestTrain("102", "Test Route")

    private val normalStops2 =
        listOf(normalTrain2.createTestStop("1", "Stop 1"),
            normalTrain2.createTestStop("2", "Stop 2"))

    private val normalTrainWithStops2 = TrainWithStops(normalTrain2, normalStops2)

    @Test
    fun testInsertTrain_normalTrain() = runTest {
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        trainDao.insertOrReplaceTrain(normalTrain2)
        trainDao.insertOrReplaceStops(normalStops2)
        val withStops1 = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops1.first(), equalTo(normalTrainWithStops1))
//        val withStops2 = trainDao.getTrainWithStops(normalTrain2.num)
//        assertThat(withStops2.first(), equalTo(normalTrainWithStops2))
    }

    @Test
    fun testInsertTrain_duplicateTrain() = runTest {
        val duplicateNumTrain = normalTrain1.copy(routeName = "Different Route")
        trainDao.insertOrReplaceTrain(duplicateNumTrain)
        trainDao.insertOrReplaceTrain(normalTrain1)
        trainDao.insertOrReplaceStops(normalStops1)
        val withStops1 = trainDao.getTrainWithStops(normalTrain1.num)
        assertThat(withStops1.first(), equalTo(normalTrainWithStops1))
    }
}