package me.cameronshaw.arrivo.data.local.model

import androidx.room.Entity
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.data.util.toOffsetDateTime

@Entity(
    primaryKeys = ["id"]
)
data class TrainEntity(
    val id: String,
    val num: String,
    val originDate: String,
    val routeName: String,
    val provider: String,
    val velocity: Double,
    val lastUpdated: String
)

/**
 * Not recommended because conversion to Train is not complete!
 */
fun TrainEntity.toDomain() = Train(
    num = num,
    routeName = routeName,
    stops = emptyList(),
    provider = provider,
    velocity = velocity,
    lastUpdated = lastUpdated.toOffsetDateTime()!!
)