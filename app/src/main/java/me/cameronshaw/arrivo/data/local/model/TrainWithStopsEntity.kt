package me.cameronshaw.arrivo.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.data.util.toOffsetDateTime

data class TrainWithStopsEntity(
    @Embedded val train: TrainEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "trainOwnerId"
    )
    val stops: List<StopEntity>
)

fun TrainWithStopsEntity.toDomain(): Train =
    Train(
        num = train.num,
        routeName = train.routeName,
        stops = stops.map { it.toDomain() },
        provider = train.provider,
        velocity = train.velocity,
        lastUpdated = train.lastUpdated.toOffsetDateTime()!!
    )
