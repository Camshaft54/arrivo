package me.cameronshaw.amtraker.widget

import kotlinx.serialization.Serializable
import me.cameronshaw.amtraker.data.model.ScheduleDatum
import me.cameronshaw.amtraker.data.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class WidgetState(
    val lastUpdated: @Serializable(with = OffsetDateTimeSerializer::class) OffsetDateTime,
    val schedule: List<ScheduleDatum>
)