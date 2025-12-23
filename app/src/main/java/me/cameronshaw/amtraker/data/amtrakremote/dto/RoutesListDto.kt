package me.cameronshaw.amtraker.data.amtrakremote.dto

import com.google.gson.annotations.SerializedName

data class RoutesListEntry(
    @SerializedName("CMSID") val cmsid: String,
    @SerializedName("Name") val name: String,
    @SerializedName("ZoomLevel") val zoomLevel: Int
)