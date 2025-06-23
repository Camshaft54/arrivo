package me.cameronshaw.amtraker.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TrainEntity::class,
            parentColumns = ["num"],
            childColumns = ["trainOwnerNum"],
            onDelete = ForeignKey.CASCADE // Delete stops when train is deleted
        )
    ]
)
data class StopEntity(
    @PrimaryKey val code: String,
    val name: String,
    val trainOwnerNum: String // Foreign key referencing the train this stop belongs to
)