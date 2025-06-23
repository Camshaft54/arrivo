package me.cameronshaw.amtraker.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrainEntity(
    @PrimaryKey val num: String,
    val routeName: String
)