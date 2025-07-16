package me.cameronshaw.amtraker.data.remote.api

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import me.cameronshaw.amtraker.data.remote.api.TestUtils.getJson
import me.cameronshaw.amtraker.data.remote.dto.TrainDto
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrainApiServiceTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: TrainApiService

    @Before
    fun setUp() {
        val trainMapType = object : TypeToken<Map<String, List<TrainDto>>>() {}.type
        val customGson = GsonBuilder()
            .registerTypeAdapter(
                trainMapType,
                MapOrEmptyArrayDeserializer(TrainDto::class.java)
            )
            .create()

        server = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(server.url("/")) // Point Retrofit to our mock server
            .addConverterFactory(GsonConverterFactory.create(customGson))
            .build()
            .create(TrainApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getTrain requests correct path and parses active train response`() = runBlocking {
        val mockResponse = MockResponse()
            .setBody(getJson("727_train_success.json"))
            .setResponseCode(200)
        server.enqueue(mockResponse)

        val expectedResponse = mapOf("727" to listOf(expected727TrainDto))

        val actualResponse = apiService.getTrain("727")

        val request = server.takeRequest()
        assertEquals("/trains/727", request.path)

        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun `getTrain requests correct path and parses inactive train response`() = runBlocking {
        val mockResponse = MockResponse()
            .setBody("[]")
            .setResponseCode(200)
        server.enqueue(mockResponse)

        val actualResponse = apiService.getTrain("999")

        val request = server.takeRequest()
        assertEquals("/trains/999", request.path)

        assertEquals(actualResponse, emptyMap<String, List<TrainDto>>())
    }

    @Test
    fun `getTrain parses train response with null arrival times`() = runBlocking {
        val mockResponse = MockResponse()
            .setBody(getJson("546_train_null_times.json"))
            .setResponseCode(200)
        server.enqueue(mockResponse)

        val expectedResponse = mapOf("546" to listOf(expected546TrainDto))

        val actualResponse = apiService.getTrain("546")

        val request = server.takeRequest()
        assertEquals("/trains/546", request.path)

        assertEquals(expectedResponse, actualResponse)
    }
}
