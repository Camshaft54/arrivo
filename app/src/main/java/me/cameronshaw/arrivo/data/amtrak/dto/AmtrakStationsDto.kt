package me.cameronshaw.arrivo.data.amtrak.dto

import com.google.gson.annotations.SerializedName
import me.cameronshaw.arrivo.data.model.Station
import java.time.OffsetDateTime

data class AmtrakStationRootResponse(
    @SerializedName("StationsDataResponse") val dataResponse: AmtrakStationsDataResponse
)

data class AmtrakStationsDataResponse(
    @SerializedName("features") val features: List<AmtrakStationFeature>
)

data class AmtrakStationFeature(
    @SerializedName("id") val id: Int,
    @SerializedName("properties") val properties: AmtrakStationProperties
)

data class AmtrakStationProperties(
    @SerializedName("StationName") val name: String,
    @SerializedName("Code") val code: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("Address1") val address1: String?,
    @SerializedName("Address2") val address2: String?,
    @SerializedName("City") val city: String?,
    @SerializedName("State") val state: String?,
    @SerializedName("Zipcode") val zip: String?
)

fun AmtrakStationProperties.toDomain() = Station(
    name = name,
    code = code,
    lastUpdated = OffsetDateTime.now()
)