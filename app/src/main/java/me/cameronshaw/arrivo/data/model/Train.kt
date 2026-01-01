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
            get() = if ((arrival == null || scheduledArrival == null) && (departure == null || scheduledDeparture == null)) {
                Status.UNKNOWN
            } else if ((arrival != null && scheduledArrival != null && arrival.isEqual(
                    scheduledArrival
                )) || (departure != null && scheduledDeparture != null && departure.isEqual(
                    scheduledDeparture
                ))
            ) {
                Status.ON_TIME
            } else if ((arrival != null && scheduledArrival != null && arrival.isAfter(
                    scheduledArrival
                )) || (departure != null && scheduledDeparture != null && departure.isEqual(
                    scheduledDeparture
                ))
            ) {
                Status.LATE
            } else {
                Status.EARLY
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