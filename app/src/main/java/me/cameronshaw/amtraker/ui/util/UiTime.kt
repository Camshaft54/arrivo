package me.cameronshaw.amtraker.ui.util

import me.cameronshaw.amtraker.data.model.Train
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun OffsetDateTime.toUiString(): String {
    return this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}

fun Train.Stop.determineDepartureStopDescription(): String =
    if (this.departure == null) {
        ""
    } else if (this.departure.isAfter(OffsetDateTime.now())) {
        "Departing"
    } else {
        "Departed"
    }

fun Train.Stop.determineArrivalStopDescription(): String =
    if (this.arrival == null) {
        ""
    } else if (this.arrival.isAfter(OffsetDateTime.now())) {
        "Arriving"
    } else {
        "Arrived"
    }