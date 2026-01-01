package me.cameronshaw.arrivo.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import me.cameronshaw.arrivo.data.model.Train
import me.cameronshaw.arrivo.data.util.toOffsetDateTime

@Entity(
    primaryKeys = ["code", "trainOwnerId"],
    foreignKeys = [
        ForeignKey(
            entity = TrainEntity::class,
            parentColumns = ["id"],
            childColumns = ["trainOwnerId"],
            onDelete = ForeignKey.CASCADE // Delete stops when train is deleted
        )
    ],
    indices = [Index(value = ["trainOwnerId"])]
)
data class StopEntity(
    val code: String,
    val name: String,
    val arrival: String,
    val departure: String,
    val scheduledArrival: String,
    val scheduledDeparture: String,
    val trainOwnerId: String, // Foreign key referencing the train this stop belongs to
)

fun StopEntity.toDomain(): Train.Stop =
    Train.Stop(
        code = code,
        name = name,
        arrival = arrival.toOffsetDateTime(),
        departure = departure.toOffsetDateTime(),
        scheduledArrival = scheduledArrival.toOffsetDateTime(),
        scheduledDeparture = scheduledDeparture.toOffsetDateTime()
    )