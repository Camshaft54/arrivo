package me.cameronshaw.amtraker.data.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun OffsetDateTime?.toDbString(): String =
    this?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) ?: ""

fun String.toOffsetDateTime(): OffsetDateTime? =
    if (this.isNotEmpty()) OffsetDateTime.parse(
        this,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME
    ) else null

val NEVER: OffsetDateTime = OffsetDateTime.parse("1970-01-01T00:00:00+00:00")