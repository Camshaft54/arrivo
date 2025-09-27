package me.cameronshaw.amtraker.widget

import kotlinx.serialization.Serializable
import me.cameronshaw.amtraker.data.local.model.AppSettings
import me.cameronshaw.amtraker.data.model.ScheduleDatum
import me.cameronshaw.amtraker.data.util.OffsetDateTimeSerializer
import java.time.OffsetDateTime

@Serializable
data class WidgetState(
    val lastUpdated: @Serializable(with = OffsetDateTimeSerializer::class) OffsetDateTime,
    val appSettings: AppSettings,
    val schedule: List<ScheduleDatum>
) {
    companion object {
        fun defaultWidgetState(): WidgetState = WidgetState(
            lastUpdated = OffsetDateTime.now(),
            appSettings = AppSettings(),
            schedule = emptyList()
        )
    }
}