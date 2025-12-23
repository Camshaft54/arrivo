package me.cameronshaw.amtraker.data.remote.amtrakapi

import kotlinx.coroutines.test.runTest
import me.cameronshaw.amtraker.data.amtrakremote.api.AmtrakApiService
import me.cameronshaw.amtraker.data.amtrakremote.api.AmtrakDecryptionInterceptor
import me.cameronshaw.amtraker.data.amtrakremote.api.AmtrakDecryptor
import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AmtrakApiServiceIntegrationTest {

    private lateinit var amtrakApiService: AmtrakApiService
    private lateinit var amtrakDecryptor: AmtrakDecryptor

    @Before
    fun setUp() = runTest {
        amtrakDecryptor = AmtrakDecryptor()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.amtrak.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val keyFetchingApiService = retrofit.create(AmtrakApiService::class.java)
        val routesList = keyFetchingApiService.getRouteList()
        val routesListValues = keyFetchingApiService.getRouteListValues()
        amtrakDecryptor.updateKeys(routesList, routesListValues)

        val client = OkHttpClient.Builder()
            .addInterceptor(AmtrakDecryptionInterceptor(amtrakDecryptor))
            .build()

        val retrofitWithDecryption = Retrofit.Builder()
            .baseUrl("https://maps.amtrak.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        amtrakApiService = retrofitWithDecryption.create(AmtrakApiService::class.java)
    }

    @Test
    fun getStations_returnsStations() = runTest {
        val stations = amtrakApiService.getStations()
        Assert.assertTrue("Stations array should not be empty", stations.dataResponse.features.map { it.properties }.isNotEmpty())
    }

    @Test
    fun getTrains_returnsTrains() = runTest {
        val trains = amtrakApiService.getTrains()
        Assert.assertTrue("Trains array should not be empty", trains.features.map { it.properties }.isNotEmpty())
    }
}
