package me.cameronshaw.arrivo.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.data.util.toOffsetDateTime

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
        provider = train.provider,
        velocity = train.velocity,
        lastUpdated = train.lastUpdated.toOffsetDateTime()!!
    )
