package me.cameronshaw.arrivo.data.model

import me.cameronshaw.arrivo.data.local.model.TrackedTrainEntity
import me.cameronshaw.arrivo.data.util.toDbString
import java.time.OffsetDateTime


data class TrackedTrain(
    val num: String,
    val originDate: OffsetDateTime?
)

fun TrackedTrain.toEntity() = TrackedTrainEntity(
    num = num,
    originDate = originDate.toDbString()
)