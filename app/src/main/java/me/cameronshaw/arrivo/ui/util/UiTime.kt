package me.cameronshaw.arrivo.ui.util

import me.cameronshaw.arrivo.data.model.Train
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import kotlin.math.abs

private fun Long.toSuperscript(): String {
    val map = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '+' to '⁺', '-' to '⁻'
    )

    val sign = when {
        this > 0 -> "⁺"
        this < 0 -> "⁻"
        else -> ""
    }

    return sign + abs(this).toString()
        .map { map[it] ?: it }
        .joinToString("")
}

fun OffsetDateTime.toUiString(): String {
    val nowAtOffset = OffsetDateTime.now().withOffsetSameInstant(this.offset).toLocalDate()
    val daysDifference = ChronoUnit.DAYS.between(nowAtOffset, this.toLocalDate())
    val dayModifier = if (daysDifference == 0L) {
        ""
    } else if (daysDifference > 0L) {
        daysDifference.toSuperscript()
    } else {
        daysDifference.toSuperscript()
    }

    val formattedTime = this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

    return formattedTime + dayModifier
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