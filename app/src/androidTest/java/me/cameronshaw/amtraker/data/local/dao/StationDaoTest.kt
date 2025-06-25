package me.cameronshaw.amtraker.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.cameronshaw.amtraker.MainCoroutineRule
import me.cameronshaw.amtraker.data.local.AppDatabase
import me.cameronshaw.amtraker.data.local.model.StationEntity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class StationDaoTest {
    private lateinit var stationDao: StationDao
    private lateinit var db: AppDatabase

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        stationDao = db.stationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private val normalStation1 = StationEntity("1", "Test Station")
    private val normalStation2 = StationEntity("2", "Test Station")

    @Test
    fun testInsertStation_normal() = runTest {
        stationDao.insertOrReplaceStation(normalStation1)
        stationDao.insertOrReplaceStation(normalStation2)
        val station1 = stationDao.getStationByCode(normalStation1.code).first()
        val station2 = stationDao.getStationByCode(normalStation2.code).first()
        assertThat(station1, equalTo(normalStation1))
        assertThat(station2, equalTo(normalStation2))
    }

    @Test
    fun testInsertStation_missingName() = runTest {
        stationDao.insertOrReplaceStation(normalStation1.copy(name = null))
        val station = stationDao.getStationByCode(normalStation1.code).first()
        assertThat(station, equalTo(normalStation1.copy(name = null)))
    }

    @Test
    fun testUpdateStation_normal() = runTest {
        stationDao.insertOrReplaceStation(normalStation1)
        val updatedStation = normalStation1.copy(name = "Updated Station")
        stationDao.updateStation(updatedStation)
        val station = stationDao.getStationByCode(normalStation1.code).first()
        assertThat(station, equalTo(updatedStation))
    }

    @Test
    fun testDeleteStation_normal() = runTest {
        stationDao.insertOrReplaceStation(normalStation1)
        stationDao.deleteStation(normalStation1)
        val stations = stationDao.getAllStations().first()
        assertThat(stations, empty())
    }

    @Test
    fun testGetAllStations_normal() = runTest {
        stationDao.insertOrReplaceStation(normalStation1)
        stationDao.insertOrReplaceStation(normalStation2)
        val stations = stationDao.getAllStations().first()
        assertThat(stations.sortedBy { it.code }, equalTo(listOf(normalStation1, normalStation2)))
    }
}