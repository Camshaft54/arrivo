package me.cameronshaw.arrivo.data.amtrak.dto

import com.google.gson.annotations.SerializedName

data class RoutesListValuesDto(
    @SerializedName("arr") val publicKeys: Array<String>,
    @SerializedName("s") val salts: Array<String>,
    @SerializedName("v") val ivs: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoutesListValuesDto

        if (!publicKeys.contentEquals(other.publicKeys)) return false
        if (!salts.contentEquals(other.salts)) return false
        if (!ivs.contentEquals(other.ivs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = publicKeys.contentHashCode()
        result = 31 * result + salts.contentHashCode()
        result = 31 * result + ivs.contentHashCode()
        return result
    }
}