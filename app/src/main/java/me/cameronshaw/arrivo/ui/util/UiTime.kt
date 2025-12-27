package me.cameronshaw.arrivo.ui.util

import me.cameronshaw.arrivo.data.model.Train
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun OffsetDateTime.toUiString(): String {
    return this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}

fun Train.Stop.determineDepartureStopDescription(): String {
    val dep = this.departure ?: this.scheduledDeparture
    return if (dep == null || dep.isAfter(OffsetDateTime.now())) {
        "Departing"
    } else {
        "Departed"
    }
}

fun Train.Stop.determineArrivalStopDescription(): String {
    val arr = this.arrival ?: this.scheduledArrival
    return if (arr == null || arr.isAfter(OffsetDateTime.now())) {
        "Arriving"
    } else {
        "Arrived"
    }
}

fun Train.Stop.determineDepartureStopFullDescription(): String {
    return if (departure != null) {
        "${determineDepartureStopDescription()} ${departure.toUiString()}"
    } else {
        "${determineDepartureStopDescription()} ${scheduledDeparture?.toUiString() ?: "--:--"}"
    }
}

fun Train.Stop.determineArrivalStopFullDescription(): String {
    return if (arrival != null) {
        "${determineArrivalStopDescription()} ${arrival.toUiString()}"
    } else {
        "${determineArrivalStopDescription()} ${scheduledArrival?.toUiString() ?: "--:--"}"
    }
}