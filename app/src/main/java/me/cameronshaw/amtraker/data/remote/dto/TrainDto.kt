package me.cameronshaw.amtraker.data.remote.dto

import com.google.gson.annotations.SerializedName
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.util.toOffsetDateTime
import java.time.OffsetDateTime

data class TrainDto(
    @SerializedName("routeName") val routeName: String,
    @SerializedName("trainNum") val trainNum: String,
    @SerializedName("trainNumRaw") val trainNumRaw: String,
    @SerializedName("trainID") val trainID: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("trainTimely") val trainTimely: String,
    @SerializedName("iconColor") val iconColor: String,
    @SerializedName("stations") val stops: List<StopDto>,
    @SerializedName("heading") val heading: String,
    @SerializedName("eventCode") val eventCode: String,
    @SerializedName("eventTZ") val eventTZ: String,
    @SerializedName("eventName") val eventName: String,
    @SerializedName("origCode") val origCode: String,
    @SerializedName("originTZ") val originTZ: String,
    @SerializedName("origName") val origName: String,
    @SerializedName("destCode") val destCode: String,
    @SerializedName("destTZ") val destTZ: String,
    @SerializedName("destName") val destName: String,
    @SerializedName("trainState") val trainState: String,
    @SerializedName("velocity") val velocity: Double,
    @SerializedName("statusMsg") val statusMsg: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("lastValTS") val lastValTS: String,
    @SerializedName("objectID") val objectID: Long,
    @SerializedName("provider") val provider: String,
    @SerializedName("providerShort") val providerShort: String,
    @SerializedName("onlyOfTrainNum") val onlyOfTrainNum: Boolean,
    @SerializedName("alerts") val alerts: List<TrainAlertDto>
) {
    data class TrainAlertDto(
        @SerializedName("message")
        val message: String
    )

    data class StopDto(
        @SerializedName("name") val name: String,
        @SerializedName("code") val code: String,
        @SerializedName("tz") val tz: String,
        @SerializedName("bus") val bus: Boolean,
        @SerializedName("schArr") val schArr: String?,
        @SerializedName("schDep") val schDep: String?,
        @SerializedName("arr") val arr: String? = null,
        @SerializedName("dep") val dep: String? = null,
        @SerializedName("arrCmnt") val arrCmnt: String?,
        @SerializedName("depCmnt") val depCmnt: String?,
        @SerializedName("platform") val platform: String?,
        @SerializedName("status") val status: String
    )
}

fun TrainDto.toDomain() = Train(
    num = trainNum,
    routeName = routeName,
    stops = stops.map { it.toDomain() },
    lastUpdated = OffsetDateTime.now()
)

fun TrainDto.StopDto.toDomain() = Train.Stop(
    name = name,
    code = code,
    arrival = arr?.toOffsetDateTime() ?: throw IllegalArgumentException("Arrival time is null"),
    departure = dep?.toOffsetDateTime() ?: throw IllegalArgumentException("Departure time is null")
)