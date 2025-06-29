package me.cameronshaw.amtraker.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.util.toOffsetDateTime

@Entity(
    primaryKeys = ["code", "trainOwnerNum"],
    foreignKeys = [
        ForeignKey(
            entity = TrainEntity::class,
            parentColumns = ["num"],
            childColumns = ["trainOwnerNum"],
            onDelete = ForeignKey.CASCADE // Delete stops when train is deleted
        )
    ]
)
data class StopEntity(
    val code: String,
    val name: String,
    val arrival: String,
    val departure: String,
    val trainOwnerNum: String // Foreign key referencing the train this stop belongs to
)

fun StopEntity.toDomain(): Train.Stop =
    Train.Stop(
        code = code,
        name = name,
        arrival = arrival.toOffsetDateTime(),
        departure = departure.toOffsetDateTime()
    )