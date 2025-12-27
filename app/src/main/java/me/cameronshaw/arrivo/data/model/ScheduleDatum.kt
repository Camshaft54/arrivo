package me.cameronshaw.arrivo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleDatum(
    val train: Train,
    val departureStop: Train.Stop?,
    val arrivalStop: Train.Stop?
)