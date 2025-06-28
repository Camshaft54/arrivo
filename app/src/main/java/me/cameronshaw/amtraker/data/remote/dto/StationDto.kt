package me.cameronshaw.amtraker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StationDto(
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String,
    @SerializedName("tz") val tz: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("hasAddress") val hasAddress: Boolean,
    @SerializedName("address1") val address1: String?,
    @SerializedName("address2") val address2: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("zip") val zip: Int?,
    @SerializedName("trains") val trains: List<String>
)