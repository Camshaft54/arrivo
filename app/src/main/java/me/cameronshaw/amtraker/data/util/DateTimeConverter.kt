package me.cameronshaw.amtraker.data.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun OffsetDateTime?.toDbString(): String =
    this?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: ""

fun String.toOffsetDateTime(): OffsetDateTime? =
    if (this.isNotEmpty()) OffsetDateTime.parse(
        this,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME
    ) else null

val NEVER: OffsetDateTime = OffsetDateTime.parse("1970-01-01T00:00:00+00:00")

private val amtrakFormatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm:ss")

fun parseAmtrakDate(dateString: String?, tz: String?): OffsetDateTime? {
    if (dateString.isNullOrBlank()) return null

    return try {
        val localDateTime = LocalDateTime.parse(dateString, amtrakFormatter)
        val offset = getOffsetForAmtrakTz(tz)
        localDateTime.atOffset(offset)
    } catch (_: Exception) {
        null
    }
}

private fun getOffsetForAmtrakTz(tz: String?): ZoneOffset {
    return when (tz?.uppercase()) {
        "E" -> ZoneOffset.ofHours(-5)
        "C" -> ZoneOffset.ofHours(-6)
        "M" -> ZoneOffset.ofHours(-7)
        "P" -> ZoneOffset.ofHours(-8)
        "A" -> ZoneOffset.ofHours(-9)
        "H" -> ZoneOffset.ofHours(-10)
        else -> ZoneOffset.UTC // Fallback to UTC
    }
}