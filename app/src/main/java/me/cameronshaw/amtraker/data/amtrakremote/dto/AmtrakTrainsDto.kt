package me.cameronshaw.amtraker.data.amtrakremote.dto

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.util.parseAmtrakDate
import java.time.OffsetDateTime

data class AmtrakTrainRootResponse(
    @SerializedName("features") val features: List<AmtrakTrainFeature>
)

data class AmtrakTrainFeature(
    @SerializedName("id") val id: Int,
    @SerializedName("properties") val properties: Map<String, Any?>
)

data class AmtrakStopDto(
    val code: String,
    val tz: String,
    val scharr: String?,
    val schdep: String?,
    val estarr: String?,
    val estdep: String?,
    val postarr: String?,
    val postdep: String?
)

fun Map<String, Any?>.toTrainDomain(
    gson: Gson
): Train {
    // 1. Extract fixed fields safely from the map
    val trainNum = this["TrainNum"]?.toString() ?: ""
    val routeName = this["RouteName"]?.toString() ?: ""
    val velocity = this["Velocity"]?.toString()?.toDoubleOrNull() ?: 0.0

    // 2. Identify, filter, and sort station fields (Station1, Station2, etc.)
    val stops = this.filterKeys { it.startsWith("Station") }
        .filterValues { it != null && it is String }
        // Ensure they are processed in numerical order (Station1, Station2...)
        .toSortedMap(compareBy { it.substringAfter("Station").toIntOrNull() ?: 0 })
        .values
        .map { jsonString ->
            val stopDto = gson.fromJson(jsonString as String, AmtrakStopDto::class.java)
//            val fullName = stationNames[stopDto.code] ?: "Unknown Station"
            stopDto.toStopDomain(stopDto.code) // TODO replace with fullname
        }

    return Train(
        num = trainNum,
        routeName = routeName,
        stops = stops,
        provider = "Amtrak",
        velocity = velocity,
        lastUpdated = OffsetDateTime.now()
    )
}

private fun AmtrakStopDto.toStopDomain(fullName: String): Train.Stop {
    val arrivalStr = this.postarr ?: this.estarr
    val departureStr = this.postdep ?: this.estdep

    return Train.Stop(
        code = this.code,
        name = fullName,
        arrival = parseAmtrakDate(arrivalStr, this.tz),
        departure = parseAmtrakDate(departureStr, this.tz),
        scheduledArrival = parseAmtrakDate(this.scharr, this.tz),
        scheduledDeparture = parseAmtrakDate(this.schdep, this.tz)
    )
}