package me.cameronshaw.amtraker.data.model

import me.cameronshaw.amtraker.data.local.model.StopEntity
import me.cameronshaw.amtraker.data.local.model.TrainEntity
import me.cameronshaw.amtraker.data.local.model.TrainWithStops
import me.cameronshaw.amtraker.data.util.toDbString
import java.time.OffsetDateTime

data class Train(
    val num: String,
    val routeName: String,
    val stops: List<Stop>
) {
    data class Stop(
        val code: String,
        val name: String,
        val arrival: OffsetDateTime,
        val departure: OffsetDateTime
    )
}

fun Train.toEntity() = TrainWithStops(
    TrainEntity(num = num, routeName = routeName),
    stops.map {
        StopEntity(
            code = it.code,
            name = it.name,
            arrival = it.arrival.toDbString(),
            departure = it.departure.toDbString(),
            trainOwnerNum = num
        )
    }
)