package me.cameronshaw.amtraker.data.model

import kotlinx.serialization.Serializable
import me.cameronshaw.amtraker.data.local.model.StopEntity
import me.cameronshaw.amtraker.data.local.model.TrainEntity
import me.cameronshaw.amtraker.data.local.model.TrainWithStops
import me.cameronshaw.amtraker.data.util.NEVER
import me.cameronshaw.amtraker.data.util.OffsetDateTimeSerializer
import me.cameronshaw.amtraker.data.util.toDbString
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@Serializable
data class Train(
    val num: String,
    val routeName: String,
    val stops: List<Stop>,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val lastUpdated: OffsetDateTime
) {
    /**
     * Data is considered "stale" if it hasn't been refreshed in over an hour.
     */
    val isStale: Boolean
        get() = ChronoUnit.HOURS.between(lastUpdated, OffsetDateTime.now()) >= 1

    @Serializable
    data class Stop(
        val code: String,
        val name: String,
        @Serializable(with = OffsetDateTimeSerializer::class)
        val arrival: OffsetDateTime?,
        @Serializable(with = OffsetDateTimeSerializer::class)
        val departure: OffsetDateTime?,
        @Serializable(with = OffsetDateTimeSerializer::class)
        val scheduledArrival: OffsetDateTime?,
        @Serializable(with = OffsetDateTimeSerializer::class)
        val scheduledDeparture: OffsetDateTime?,
    ) {
        val status: Status
            get() = if (arrival == null || scheduledArrival == null) {
                Status.UNKNOWN
            } else if (arrival.isEqual(scheduledArrival)) {
                Status.ON_TIME
            } else if (arrival.isAfter(scheduledArrival)) {
                Status.LATE
            } else {
                Status.EARLY
            }
    }

    constructor(num: String) : this(num, "", emptyList(), NEVER)
}

fun Train.toEntity() = TrainWithStops(
    TrainEntity(num = num, routeName = routeName, lastUpdated = lastUpdated.toDbString()),
    stops.map {
        StopEntity(
            code = it.code,
            name = it.name,
            arrival = it.arrival.toDbString(),
            departure = it.departure.toDbString(),
            scheduledArrival = it.scheduledArrival.toDbString(),
            scheduledDeparture = it.scheduledDeparture.toDbString(),
            trainOwnerNum = num
        )
    }
)