package me.cameronshaw.amtraker.ui.util

import me.cameronshaw.amtraker.data.model.Train
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun OffsetDateTime.toUiString(): String {
    return this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}

fun Train.Stop.determineDepartureStopDescription(): String {
    val dep = this.departure ?: this.scheduledDeparture
    return if (dep == null) {
        ""
    } else if (dep.isAfter(OffsetDateTime.now())) {
        "Departing"
    } else {
        "Departed"
    }
}

fun Train.Stop.determineArrivalStopDescription(): String {
    val arr = this.arrival ?: this.scheduledArrival
    return if (arr == null) {
        ""
    } else if (arr.isAfter(OffsetDateTime.now())) {
        "Arriving"
    } else {
        "Arrived"
    }
}