package me.cameronshaw.amtraker.data.model

import me.cameronshaw.amtraker.data.local.model.StationEntity
import me.cameronshaw.amtraker.data.util.NEVER
import me.cameronshaw.amtraker.data.util.toDbString
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

data class Station(
    val code: String,
    val name: String,
    val lastUpdated: OffsetDateTime
) {
    /**
     * Data is considered "stale" if it hasn't been refreshed in over an hour.
     */
    val isStale: Boolean
        get() = ChronoUnit.HOURS.between(lastUpdated, OffsetDateTime.now()) >= 1

    constructor(code: String) : this(code, "", NEVER)
}

fun Station.toEntity() = StationEntity(
    code = code,
    name = name,
    lastUpdated = lastUpdated.toDbString()
)