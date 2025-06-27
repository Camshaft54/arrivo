package me.cameronshaw.amtraker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TrainDto(
    @SerializedName("routeName") val routeName: String,
    @SerializedName("trainNum") val trainNum: String,
    @SerializedName("trainNumRaw") val trainNumRaw: String,
    @SerializedName("trainID") val trainID: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("trainTimely") val trainTimely: String,
    @SerializedName("iconColor") val iconColor: String,
    @SerializedName("stations") val stations: List<StationDto>,
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

    // Timestamps are strings in the JSON, so they are kept as String here.
    // They can be converted to more specific Date/Time objects in the repository layer.
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("lastValTS") val lastValTS: String,

    @SerializedName("objectID") val objectID: Long,
    @SerializedName("provider") val provider: String,
    @SerializedName("providerShort") val providerShort: String,
    @SerializedName("onlyOfTrainNum") val onlyOfTrainNum: Boolean,
    @SerializedName("alerts") val alerts: List<TrainAlertDto>
)
