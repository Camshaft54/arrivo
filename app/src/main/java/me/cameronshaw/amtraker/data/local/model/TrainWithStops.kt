package me.cameronshaw.amtraker.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class TrainWithStops(
    @Embedded val train: TrainEntity,
    @Relation(
        parentColumn = "num",
        entityColumn = "trainOwnerNum"
    )
    val stops: List<StopEntity>
)
