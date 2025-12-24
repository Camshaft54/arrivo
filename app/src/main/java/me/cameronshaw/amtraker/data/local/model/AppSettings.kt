package me.cameronshaw.amtraker.data.local.model

import kotlinx.serialization.Serializable

val THEMES = listOf("SYSTEM", "LIGHT", "DARK")

val PROVIDERS = listOf("AMTRAK", "AMTRAKER")

@Serializable
data class AppSettings(
    val theme: String = THEMES[0],
    val dataProvider: String = PROVIDERS[0]
)