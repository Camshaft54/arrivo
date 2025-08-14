package me.cameronshaw.amtraker.data.model

data class ScheduleDatum(
    val train: Train,
    val departureStop: Train.Stop?,
    val arrivalStop: Train.Stop?
)