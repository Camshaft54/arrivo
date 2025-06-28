package me.cameronshaw.amtraker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.cameronshaw.amtraker.data.model.Station

@Entity
data class StationEntity(
    @PrimaryKey val code: String,
    val name: String
)

fun StationEntity.toDomain(): Station {
    return Station(
        code = code,
        name = name
    )
}