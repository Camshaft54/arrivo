package me.cameronshaw.arrivo.data.amtrak.dto

import com.google.gson.annotations.SerializedName

data class RoutesListValuesDto(
    @SerializedName("arr") val publicKeys: Array<String>,
    @SerializedName("s") val salts: Array<String>,
    @SerializedName("v") val ivs: Array<String>
)