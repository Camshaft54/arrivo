package me.cameronshaw.amtraker.data.remote.dto

data class StationDto(
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String,
    @SerializedName("tz") val tz: String,
    @SerializedName("bus") val bus: Boolean,
    @SerializedName("schArr") val schArr: String?,
    @SerializedName("schDep") val schDep: String?,
    @SerializedName("arr") val arr: String?,
    @SerializedName("dep") val dep: String?,
    @SerializedName("arrCmnt") val arrCmnt: String?,
    @SerializedName("depCmnt") val depCmnt: String?,
    @SerializedName("platform") val platform: String?,
    @SerializedName("status") val status: String
)