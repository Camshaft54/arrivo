package me.cameronshaw.arrivo.data.model

import kotlinx.serialization.Serializable
import me.cameronshaw.arrivo.data.local.model.StopEntity
import me.cameronshaw.arrivo.data.local.model.TrainEntity
import me.cameronshaw.arrivo.data.local.model.TrainWithStopsEntity
import me.cameronshaw.arrivo.data.util.NEVER
import me.cameronshaw.arrivo.data.util.OffsetDateTimeSerializer
import me.cameronshaw.arrivo.data.util.toDbString
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

const val LATE_THRESHOLD_SECONDS = 179L
const val EARLY_THRESHOLD_SECONDS = 119L

@Serializable
data class Train(
    val num: String,
    val routeName: String,
    val stops: List<Stop>,
    val provider: String,
    val velocity: Double,
    @Serializable(with = OffsetDateTimeSerializer::class) val lastUpdated: OffsetDateTime
) {
    val id: String
        get() = "$num (${originDate.toDbString()})"

    @Serializable(with = OffsetDateTimeSerializer::class)
    val originDate: OffsetDateTime?
        get() = stops.firstOrNull()?.departure?.truncatedTo(ChronoUnit.DAYS)


    /**
     * Data is considered "stale" if it hasn't been refreshed in over an hour.
     */
    val isStale: Boolean
        get() = ChronoUnit.HOURS.between(lastUpdated, OffsetDateTime.now()) >= 1

    @Serializable
    data class Stop(
        val code: String,
        val name: String,
        @Serializable(with = OffsetDateTimeSerializer::class) val arrival: OffsetDateTime?,
        @Serializable(with = OffsetDateTimeSerializer::class) val departure: OffsetDateTime?,
        @Serializable(with = OffsetDateTimeSerializer::class) val scheduledArrival: OffsetDateTime?,
        @Serializable(with = OffsetDateTimeSerializer::class) val scheduledDeparture: OffsetDateTime?,
    ) {
        val status: Status
            get() {
                val arrivalStatus = computeStatus(scheduledArrival, arrival)
                val departureStatus = computeStatus(scheduledDeparture, departure)

                return if (arrivalStatus != Status.UNKNOWN) arrivalStatus else departureStatus
            }

        val arrivedAt: Boolean
            get() = arrival?.isBefore(OffsetDateTime.now()) ?: departedFrom

        val departedFrom: Boolean
            get() = departure?.isBefore(OffsetDateTime.now()) ?: false
    }

    constructor(
        num: String
    ) : this(num, "", emptyList(), "", 0.0, NEVER)
}

fun Train.toEntity() = TrainWithStopsEntity(
    TrainEntity(
        id = id,
        num = num,
        originDate = originDate.toDbString(),
        routeName = routeName,
        provider = provider,
        velocity = velocity,
        lastUpdated = lastUpdated.toDbString()
    ),
    stops.map {
        StopEntity(
            code = it.code,
            name = it.name,
            arrival = it.arrival.toDbString(),
            departure = it.departure.toDbString(),
            scheduledArrival = it.scheduledArrival.toDbString(),
            scheduledDeparture = it.scheduledDeparture.toDbString(),
            trainOwnerId = id
        )
    })

private fun computeStatus(scheduled: OffsetDateTime?, actual: OffsetDateTime?): Status =
    if (actual == null || scheduled == null) {
        Status.UNKNOWN
    } else if (scheduled.minus(EARLY_THRESHOLD_SECONDS, ChronoUnit.SECONDS).isAfter(actual)) {
        Status.EARLY
    } else if (scheduled.plus(LATE_THRESHOLD_SECONDS, ChronoUnit.SECONDS).isBefore(actual)) {
        Status.LATE
    } else {
        Status.ON_TIME
    }