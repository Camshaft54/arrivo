package me.cameronshaw.arrivo.data.local.model

import kotlinx.serialization.Serializable
import me.cameronshaw.arrivo.data.util.NEVER
import me.cameronshaw.arrivo.data.util.OffsetDateTimeSerializer
import me.cameronshaw.arrivo.data.util.isTimeStale
import java.time.OffsetDateTime

val THEMES = listOf("SYSTEM", "LIGHT", "DARK")

val PROVIDERS = listOf("AMTRAK", "AMTRAKER")

@Serializable
data class AppSettings(
    val theme: String = THEMES[0],
    val dataProvider: String = PROVIDERS[0],
    @Serializable(with = OffsetDateTimeSerializer::class) val trainsLastUpdated: OffsetDateTime = NEVER
) {
    val isTrainsStale: Boolean
        get() = trainsLastUpdated.isTimeStale()
}