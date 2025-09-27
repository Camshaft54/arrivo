package me.cameronshaw.amtraker.data.local.model

import kotlinx.serialization.Serializable

val THEMES = listOf("SYSTEM", "LIGHT", "DARK")

@Serializable
data class AppSettings(
    val theme: String = THEMES[0]
)