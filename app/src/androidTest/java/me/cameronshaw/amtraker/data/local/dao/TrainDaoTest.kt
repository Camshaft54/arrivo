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

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runTest {
        val train = TrainEntity("546", "Capitol Corridor")
        val stop1 = StopEntity("1", "Stop 1", "546")
        val trainWithStops = TrainWithStops(train, listOf(stop1))
        trainDao.insertOrReplaceTrain(train)
        trainDao.insertOrReplaceStops(listOf(stop1))
        val withStops = trainDao.loadWithStops("546")
        assertThat(withStops.first(), equalTo(trainWithStops))
    }
}