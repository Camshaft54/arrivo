package me.cameronshaw.amtraker.data.model

import java.time.LocalDateTime

data class Train(
    val num: String,
    val routeName: String,
    val stops: List<Stop>
) {
    data class Stop(
        val code: String,
        val name: String,
        val arrival: LocalDateTime,
        val departure: LocalDateTime
    )

}