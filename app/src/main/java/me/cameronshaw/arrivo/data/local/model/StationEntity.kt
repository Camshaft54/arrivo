package me.cameronshaw.arrivo.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.cameronshaw.arrivo.data.model.Station
import me.cameronshaw.arrivo.data.util.toOffsetDateTime

@Entity
data class StationEntity(
    @PrimaryKey val code: String,
    val name: String,
    val lastUpdated: String
)

fun StationEntity.toDomain(): Station {
    return Station(
        code = code,
        name = name,
        lastUpdated = lastUpdated.toOffsetDateTime()!!
    )
}