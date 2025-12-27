package me.cameronshaw.arrivo.data.model

enum class Status(val description: String) {
    EARLY("Early"),
    ON_TIME("On Time"),
    LATE("Late"),
    UNKNOWN("Unknown")
}