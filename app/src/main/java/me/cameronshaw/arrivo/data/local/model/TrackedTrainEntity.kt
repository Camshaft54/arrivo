package me.cameronshaw.arrivo.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.cameronshaw.arrivo.data.model.TrackedTrain
import me.cameronshaw.arrivo.data.util.toOffsetDateTime

@Entity
data class TrackedTrainEntity(
    @PrimaryKey val num: String,
    val originDate: String
)

fun TrackedTrainEntity.toDomain() = TrackedTrain(
    num = num,
    originDate = originDate.toOffsetDateTime()
)