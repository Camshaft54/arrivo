package me.cameronshaw.amtraker.data.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun OffsetDateTime.toDbString(): String = this.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

fun String.toOffsetDateTime(): OffsetDateTime =
    OffsetDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)