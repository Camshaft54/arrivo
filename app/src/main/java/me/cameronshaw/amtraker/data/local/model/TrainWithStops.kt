package me.cameronshaw.amtraker.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.util.toOffsetDateTime

data class TrainWithStops(
    @Embedded val train: TrainEntity,
    @Relation(
        parentColumn = "num",
        entityColumn = "trainOwnerNum"
    )
    val stops: List<StopEntity>
)

fun TrainWithStops.toDomain(): Train =
    Train(
        num = train.num,
        routeName = train.routeName,
        stops = stops.map { it.toDomain() },
        lastUpdated = train.lastUpdated.toOffsetDateTime()!!
    )
