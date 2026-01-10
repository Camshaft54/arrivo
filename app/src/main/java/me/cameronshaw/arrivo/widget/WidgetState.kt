package me.cameronshaw.arrivo.widget

import kotlinx.serialization.Serializable
import me.cameronshaw.arrivo.data.local.model.AppSettings
import me.cameronshaw.arrivo.data.model.ScheduleDatum

@Serializable
data class WidgetState(
    val appSettings: AppSettings,
    val schedule: List<ScheduleDatum>
) {
    companion object {
        fun defaultWidgetState(): WidgetState = WidgetState(
            appSettings = AppSettings(),
            schedule = emptyList()
        )
    }
}