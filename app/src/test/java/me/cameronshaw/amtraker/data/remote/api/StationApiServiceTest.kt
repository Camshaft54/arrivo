package me.cameronshaw.amtraker.data.remote.api

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import me.cameronshaw.amtraker.data.remote.api.TestUtils.getJson
import me.cameronshaw.amtraker.data.remote.dto.StationDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StationApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: StationApiService

    @Before
    fun setUp() {
        val stationMapType = object : TypeToken<Map<String, List<StationDto>>>() {}.type
        val customGson = GsonBuilder()
            .registerTypeAdapter(
                stationMapType,
                MapOrEmptyArrayDeserializer(StationDto::class.java)
            )
            .create()

        server = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create(customGson))
            .build()
            .create(StationApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getStation requests correct path and parses active station`() = runBlocking {
        val mockResponse = MockResponse()
            .setBody(getJson("GAC_station_success.json"))
            .setResponseCode(200)
        server.enqueue(mockResponse)

        val expectedResponse = mapOf("GAC" to expectedGACStationDto)

        val actualResponse = apiService.getStation("GAC")

        val request = server.takeRequest()
        assertEquals("/stations/GAC", request.path)

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `getStation requests correct path and parses nonexistent station`() = runBlocking {
        val mockResponse = MockResponse()
            .setBody("[]")
            .setResponseCode(200)
        server.enqueue(mockResponse)

        val actualResponse = apiService.getStation("ABC")

        val request = server.takeRequest()
        assertEquals("/stations/ABC", request.path)

        assertEquals(actualResponse, emptyMap<String, StationDto>())
    }
}
