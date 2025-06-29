package me.cameronshaw.amtraker.data.model

import me.cameronshaw.amtraker.data.local.model.StationEntity

data class Station(
    val code: String,
    val name: String
)

fun Station.toEntity() = StationEntity(
    code = code,
    name = name
)