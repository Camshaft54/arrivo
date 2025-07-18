package me.cameronshaw.amtraker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.cameronshaw.amtraker.data.model.Train
import me.cameronshaw.amtraker.data.util.toOffsetDateTime

@Entity
data class TrainEntity(
    @PrimaryKey val num: String,
    val routeName: String,
    val lastUpdated: String
)

/**
 * Not recommended because conversion to Train is not complete!
 */
fun TrainEntity.toDomain() = Train(
    num = num,
    routeName = routeName,
    stops = emptyList(),
    lastUpdated = lastUpdated.toOffsetDateTime()!!
)